package com.ovais.ndjsonparser.data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.model.ParseResult
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull
import java.io.BufferedReader
import java.io.OutputStreamWriter

/**
 * Repository implementation for NDJSON operations
 * Follows Dependency Inversion Principle (SOLID)
 */
class NDJsonRepositoryImpl(
    private val context: Context
) : NDJsonRepository {

    private val contentResolver: ContentResolver = context.contentResolver
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    override suspend fun parseNDJsonFile(uri: Uri): ParseResult = withContext(Dispatchers.IO) {
        try {
            val jsonObjects = mutableListOf<JsonObject>()
            var errorCount = 0
            var totalLines = 0
            val errorMessages = mutableListOf<String>()

            contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(inputStream.reader()).use { reader ->
                    reader.lineSequence().forEachIndexed { index, line ->
                        totalLines++
                        if (line.isNotBlank()) {
                            try {
                                val trimmedLine = line.trim()
                                val jsonElement = json.parseToJsonElement(trimmedLine)

                                // Try to get JSON object - this will throw if not an object
                                val kJsonObject = try {
                                    jsonElement.jsonObject
                                } catch (e: Exception) {
                                    errorCount++
                                    errorMessages.add("Line ${index + 1}: Not a valid JSON object - ${e.message}")
                                    null
                                }

                                if (kJsonObject != null) {
                                    if (kJsonObject.isNotEmpty()) {
                                        val map = mutableMapOf<String, Any?>()

                                        kJsonObject.forEach { (key, value) ->
                                            map[key] = extractValue(value)
                                        }

                                        jsonObjects.add(JsonObject(map, trimmedLine))
                                    } else {
                                        errorCount++
                                        errorMessages.add("Line ${index + 1}: Empty JSON object")
                                    }
                                }
                            } catch (e: Exception) {
                                errorCount++
                                errorMessages.add("Line ${index + 1}: ${e.message ?: "Parse error"}")
                                android.util.Log.e(
                                    "NDJsonParser",
                                    "Error parsing line ${index + 1}: ${e.message}",
                                    e
                                )
                            }
                        }
                    }
                }
            }

            if (jsonObjects.isEmpty() && totalLines > 0) {
                val errorMsg = if (errorMessages.isNotEmpty()) {
                    "Failed to parse file. Errors:\n${errorMessages.take(5).joinToString("\n")}" +
                            if (errorMessages.size > 5) "\n... and ${errorMessages.size - 5} more errors" else ""
                } else {
                    "Failed to parse any valid JSON lines. Check file format."
                }
                ParseResult.Error(errorMsg)
            } else if (jsonObjects.isNotEmpty()) {
                ParseResult.Success(jsonObjects)
            } else {
                ParseResult.Error("File is empty or contains no valid JSON objects.")
            }
        } catch (e: Exception) {
            ParseResult.Error("Error reading file: ${e.message ?: "Unknown error"}")
        }
    }

    private fun extractValue(value: kotlinx.serialization.json.JsonElement): Any? {
        return when {
            value is kotlinx.serialization.json.JsonPrimitive -> {
                when {
                    value.isString -> value.content
                    value.booleanOrNull != null -> value.boolean
                    value.longOrNull != null -> value.long
                    value.doubleOrNull != null -> {
                        val num = value.doubleOrNull
                        // If it's a whole number, return as Long
                        if (num != null && num.rem(1) == 0.0) {
                            num.toLong()
                        } else {
                            num
                        }
                    }

                    else -> value.content
                }
            }

            value is kotlinx.serialization.json.JsonNull -> null
            else -> {
                // For arrays and nested objects, convert to string
                try {
                    value.toString()
                } catch (e: Exception) {
                    value.toString()
                }
            }
        }
    }

    override suspend fun convertToJson(jsonObjects: List<JsonObject>): String =
        withContext(Dispatchers.Default) {
            val jsonArray = jsonObjects.map { it.data }
            val jsonString = StringBuilder()
            jsonString.append("[\n")
            jsonArray.forEachIndexed { index, map ->
                jsonString.append("  {\n")
                map.entries.forEachIndexed { entryIndex, entry ->
                    val value = when (entry.value) {
                        null -> "null"
                        is String -> "\"${entry.value.toString().replace("\"", "\\\"")}\""
                        else -> entry.value.toString()
                    }
                    jsonString.append("    \"${entry.key}\": $value")
                    if (entryIndex < map.size - 1) jsonString.append(",")
                    jsonString.append("\n")
                }
                jsonString.append("  }")
                if (index < jsonArray.size - 1) jsonString.append(",")
                jsonString.append("\n")
            }
            jsonString.append("]")
            jsonString.toString()
        }

    override suspend fun convertToCsv(jsonObjects: List<JsonObject>): String =
        withContext(Dispatchers.Default) {
            if (jsonObjects.isEmpty()) return@withContext ""

            // Collect all unique keys from all objects
            val allKeys = jsonObjects.flatMap { it.data.keys }.distinct().sorted()

            val csv = StringBuilder()

            // Write header
            csv.append(allKeys.joinToString(",") { "\"$it\"" })
            csv.append("\n")

            // Write data rows
            jsonObjects.forEach { jsonObject ->
                val values = allKeys.map { key ->
                    val value = jsonObject.data[key]
                    when (value) {
                        null -> ""
                        is String -> {
                            // Escape quotes and wrap in quotes
                            val escaped = value.replace("\"", "\"\"")
                            "\"$escaped\""
                        }

                        else -> "\"${value.toString()}\""
                    }
                }
                csv.append(values.joinToString(","))
                csv.append("\n")
            }

            csv.toString()
        }

    override suspend fun filterByKey(
        jsonObjects: List<JsonObject>,
        key: String,
        value: String?
    ): List<JsonObject> = withContext(Dispatchers.Default) {
        val trimmedKey = key.trim()
        if (trimmedKey.isBlank()) return@withContext jsonObjects

        val trimmedValue = value?.trim()
        
        jsonObjects.filter { jsonObject ->
            // Check if the key exists in this object
            if (!jsonObject.data.containsKey(trimmedKey)) {
                false
            } else {
                val fieldValue = jsonObject.data[trimmedKey]
                
                when {
                    // If value is null or blank, return items where key exists and is not null
                    trimmedValue.isNullOrBlank() -> {
                        fieldValue != null
                    }
                    // If value is provided, check for match
                    else -> {
                        if (fieldValue == null) {
                            false
                        } else {
                            val fieldStr = fieldValue.toString().trim()
                            // Check for exact match or contains match (case-insensitive)
                            fieldStr.equals(trimmedValue, ignoreCase = true) ||
                                    fieldStr.contains(trimmedValue, ignoreCase = true)
                        }
                    }
                }
            }
        }
    }

    override suspend fun saveFile(
        content: String,
        fileName: String,
        format: FileFormat
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val mimeType = when (format) {
                FileFormat.JSON -> "application/json"
                FileFormat.CSV -> "text/csv"
            }

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/NDJsonParser")
                }
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                }
                contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            }

            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(content)
                    }
                }
                Result.success(it)
            } ?: Result.failure(Exception("Failed to create file"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

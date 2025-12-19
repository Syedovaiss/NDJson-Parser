package com.ovais.ndjsonparser.domain.repository

import android.net.Uri
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.model.ParseResult

/**
 * Repository interface for NDJSON operations
 * Follows Interface Segregation Principle (SOLID)
 */
interface NDJsonRepository {
    suspend fun parseNDJsonFile(uri: Uri): ParseResult
    suspend fun convertToJson(jsonObjects: List<JsonObject>): String
    suspend fun convertToCsv(jsonObjects: List<JsonObject>): String
    suspend fun filterByKey(jsonObjects: List<JsonObject>, key: String, value: String?): List<JsonObject>
    suspend fun saveFile(content: String, fileName: String, format: com.ovais.ndjsonparser.domain.model.FileFormat): Result<Uri>
}


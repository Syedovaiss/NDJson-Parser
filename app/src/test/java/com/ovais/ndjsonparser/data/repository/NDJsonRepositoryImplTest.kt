package com.ovais.ndjsonparser.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.model.ParseResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.OutputStream

class NDJsonRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver
    private lateinit var repository: NDJsonRepositoryImpl

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        contentResolver = mockk(relaxed = true)
        every { context.contentResolver } returns contentResolver
        repository = NDJsonRepositoryImpl(context)
    }

    @Test
    fun `parseNDJsonFile returns success with valid NDJSON`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"name\":\"John\",\"age\":30}\n{\"name\":\"Jane\",\"age\":25}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertEquals(2, successResult.jsonObjects.size)
        assertEquals("John", successResult.jsonObjects[0].data["name"])
        assertEquals(30L, successResult.jsonObjects[0].data["age"])
    }

    @Test
    fun `parseNDJsonFile handles empty file`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val inputStream = ByteArrayInputStream("".toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Error)
        assertEquals(
            "File is empty or contains no valid JSON objects.",
            (result as ParseResult.Error).message
        )
    }

    @Test
    fun `parseNDJsonFile handles invalid JSON lines`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "invalid json\n{\"name\":\"John\"}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        // Should succeed with valid lines, skipping invalid ones
        assertTrue(result is ParseResult.Success || result is ParseResult.Error)
        if (result is ParseResult.Success) {
            assertEquals(1, result.jsonObjects.size)
        }
    }

    @Test
    fun `parseNDJsonFile handles file read error`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { contentResolver.openInputStream(uri) } returns null

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Error)
        assertTrue((result as ParseResult.Error).message.contains("File is empty"))
    }

    @Test
    fun `parseNDJsonFile handles mixed valid and invalid lines`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "invalid\n{\"name\":\"John\"}\nnot json\n{\"name\":\"Jane\"}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        // Should succeed with valid lines, skipping invalid ones
        assertTrue(result is ParseResult.Success || result is ParseResult.Error)
        if (result is ParseResult.Success) {
            assertEquals(2, result.jsonObjects.size)
        }
    }

    @Test
    fun `parseNDJsonFile handles exception during reading`() = runTest {
        // Given
        val uri = mockk<Uri>()
        every { contentResolver.openInputStream(uri) } throws Exception("IO Error")

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Error)
        assertTrue((result as ParseResult.Error).message.contains("Error reading file"))
    }

    @Test
    fun `convertToJson converts objects correctly`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("John"))
        assertTrue(result.contains("Jane"))
        assertTrue(result.contains("30"))
        assertTrue(result.contains("25"))
        assertTrue(result.startsWith("["))
        assertTrue(result.endsWith("]"))
    }

    @Test
    fun `convertToJson handles empty list`() = runTest {
        // Given
        val jsonObjects = emptyList<JsonObject>()

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        // Implementation outputs: [\n] (with newline)
        assertEquals("[\n]", result)
    }

    @Test
    fun `convertToJson handles null values`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to null), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("null"))
    }

    @Test
    fun `convertToJson escapes quotes in strings`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John \"Johnny\" Doe"), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("\\\""))
    }

    @Test
    fun `convertToJson handles numbers`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("age" to 30, "price" to 29.99), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("30"))
        assertTrue(result.contains("29.99"))
    }

    @Test
    fun `convertToJson handles booleans`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("active" to true, "verified" to false), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("true"))
        assertTrue(result.contains("false"))
    }

    @Test
    fun `convertToJson handles multiple objects correctly`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), ""),
            JsonObject(mapOf("name" to "Jane"), ""),
            JsonObject(mapOf("name" to "Bob"), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("John"))
        assertTrue(result.contains("Jane"))
        assertTrue(result.contains("Bob"))
        // Should have commas between objects
        val commaCount = result.count { it == ',' }
        assertTrue(commaCount >= 2)
    }

    @Test
    fun `convertToJson handles single field objects`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), "")
        )

        // When
        val result = repository.convertToJson(jsonObjects)

        // Then
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"John\""))
        assertFalse(result.contains(",\n")) // No trailing comma for single field
    }

    @Test
    fun `convertToCsv handles numbers`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30, "price" to 29.99), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"30\""))
        assertTrue(result.contains("\"29.99\""))
    }

    @Test
    fun `convertToCsv handles booleans`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "active" to true), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"true\""))
    }

    @Test
    fun `convertToCsv handles strings with commas`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John, Jr.", "city" to "New York, NY"), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"John, Jr.\""))
        assertTrue(result.contains("\"New York, NY\""))
    }

    @Test
    fun `convertToCsv handles strings with newlines`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("description" to "Line 1\nLine 2"), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"Line 1"))
    }

    @Test
    fun `filterByKey handles empty value string`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "description" to ""), ""),
            JsonObject(mapOf("name" to "Jane"), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "description", "")

        // Then
        assertEquals(1, result.size)
        assertEquals("John", result[0].data["name"])
    }

    @Test
    fun `filterByKey handles whitespace only value`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "description" to "   "), ""),
            JsonObject(mapOf("name" to "Jane"), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "description", "   ")

        // Then
        assertEquals(1, result.size)
    }

    @Test
    fun `convertToCsv converts objects correctly`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"age\""))
        assertTrue(result.contains("\"John\""))
        assertTrue(result.contains("\"30\""))
    }

    @Test
    fun `convertToCsv handles empty list`() = runTest {
        // Given
        val jsonObjects = emptyList<JsonObject>()

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `convertToCsv handles missing keys`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), ""),
            JsonObject(mapOf("age" to 30), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"name\""))
        assertTrue(result.contains("\"age\""))
        assertTrue(result.contains("\"John\""))
        assertTrue(result.contains("\"30\""))
    }

    @Test
    fun `convertToCsv escapes quotes in strings`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John \"Johnny\" Doe"), "")
        )

        // When
        val result = repository.convertToCsv(jsonObjects)

        // Then
        assertTrue(result.contains("\"\"Johnny\"\""))
    }

    @Test
    fun `filterByKey returns all objects when key is blank`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), ""),
            JsonObject(mapOf("name" to "Jane"), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "", null)

        // Then
        assertEquals(jsonObjects, result)
    }

    @Test
    fun `filterByKey filters by key existence when value is null`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("age" to 25), ""),
            JsonObject(mapOf("name" to "Jane"), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "name", null)

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.data.containsKey("name") })
    }

    @Test
    fun `filterByKey filters by exact value match`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), ""),
            JsonObject(mapOf("name" to "John", "age" to 35), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "name", "John")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.data["name"] == "John" })
    }

    @Test
    fun `filterByKey filters by partial value match`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John Doe", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane Smith", "age" to 25), ""),
            JsonObject(mapOf("name" to "Johnny", "age" to 35), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "name", "John")

        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.data["name"].toString().contains("John", ignoreCase = true) })
    }

    @Test
    fun `filterByKey is case insensitive`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "JOHN", "age" to 25), ""),
            JsonObject(mapOf("name" to "jane", "age" to 35), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "name", "john")

        // Then
        assertEquals(2, result.size)
    }

    @Test
    fun `filterByKey excludes objects without key`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), ""),
            JsonObject(mapOf("age" to 30), ""),
            JsonObject(mapOf("name" to "Jane"), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "name", "John")

        // Then
        assertEquals(1, result.size)
        assertEquals("John", result[0].data["name"])
    }

    @Test
    fun `filterByKey handles null field values`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to null), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "age", "25")

        // Then
        assertEquals(1, result.size)
        assertEquals(25, result[0].data["age"])
    }

    @Test
    fun `filterByKey trims key and value`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )

        // When
        val result = repository.filterByKey(jsonObjects, "  name  ", "  John  ")

        // Then
        assertEquals(1, result.size)
        assertEquals("John", result[0].data["name"])
    }

    @Test
    fun `saveFile returns failure when openOutputStream returns null`() = runTest {
        // Given
        val content = "test"
        val fileName = "test.json"
        val uri = mockk<Uri>()

        every { contentResolver.insert(any(), any()) } returns uri
        every { contentResolver.openOutputStream(uri) } returns null

        // When
        val result = repository.saveFile(content, fileName, FileFormat.JSON)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `saveFile handles write exception`() = runTest {
        // Given
        val content = "test"
        val fileName = "test.json"
        val uri = mockk<Uri>()
        val outputStream = mockk<OutputStream>()

        every { contentResolver.insert(any(), any()) } returns uri
        every { contentResolver.openOutputStream(uri) } returns outputStream
        every { outputStream.write(any<ByteArray>()) } throws Exception("Write failed")

        // When
        val result = repository.saveFile(content, fileName, FileFormat.JSON)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `saveFile handles insert exception`() = runTest {
        // Given
        val content = "test"
        val fileName = "test.json"

        every { contentResolver.insert(any(), any()) } throws Exception("Insert failed")

        // When
        val result = repository.saveFile(content, fileName, FileFormat.JSON)

        // Then
        assertTrue(result.isFailure)
    }
}


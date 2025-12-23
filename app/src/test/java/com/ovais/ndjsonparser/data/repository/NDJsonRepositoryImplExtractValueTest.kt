package com.ovais.ndjsonparser.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.ovais.ndjsonparser.domain.model.ParseResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

/**
 * Tests for extractValue function through parseNDJsonFile
 * This tests various JSON value types: strings, numbers, booleans, nulls, arrays, objects
 */
class NDJsonRepositoryImplExtractValueTest {

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
    fun `parseNDJsonFile extracts string values correctly`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"name\":\"John\",\"city\":\"New York\"}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertEquals("John", successResult.jsonObjects[0].data["name"])
        assertEquals("New York", successResult.jsonObjects[0].data["city"])
    }

    @Test
    fun `parseNDJsonFile extracts integer values as Long`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"age\":30,\"count\":100}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertTrue(successResult.jsonObjects[0].data["age"] is Long)
        assertEquals(30L, successResult.jsonObjects[0].data["age"])
    }

    @Test
    fun `parseNDJsonFile extracts double values correctly`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"price\":29.99,\"temperature\":-5.5}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertTrue(successResult.jsonObjects[0].data["price"] is Double)
        assertEquals(29.99, successResult.jsonObjects[0].data["price"] as Double, 0.01)
    }

    @Test
    fun `parseNDJsonFile extracts whole number doubles as Long`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"count\":100.0}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertTrue(successResult.jsonObjects[0].data["count"] is Long)
        assertEquals(100L, successResult.jsonObjects[0].data["count"])
    }

    @Test
    fun `parseNDJsonFile extracts boolean values correctly`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"active\":true,\"verified\":false}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertTrue(successResult.jsonObjects[0].data["active"] is Boolean)
        assertEquals(true, successResult.jsonObjects[0].data["active"])
        assertEquals(false, successResult.jsonObjects[0].data["verified"])
    }

    @Test
    fun `parseNDJsonFile extracts array values as string`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"tags\":[\"tag1\",\"tag2\"]}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertTrue(successResult.jsonObjects[0].data["tags"] is String)
        assertTrue(successResult.jsonObjects[0].data["tags"].toString().contains("tag1"))
    }

    @Test
    fun `parseNDJsonFile extracts nested object values as string`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"address\":{\"street\":\"123 Main\",\"city\":\"NYC\"}}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertTrue(successResult.jsonObjects[0].data["address"] is String)
    }

    @Test
    fun `parseNDJsonFile handles multiple lines with mixed types`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = """
            {"name":"John","age":30,"active":true}
            {"name":"Jane","age":25.5,"active":false,"tags":null}
        """.trimIndent()
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertEquals(2, successResult.jsonObjects.size)
        assertEquals(30L, successResult.jsonObjects[0].data["age"])
        assertEquals(25.5, successResult.jsonObjects[1].data["age"] as Double, 0.01)
    }

    @Test
    fun `parseNDJsonFile handles blank lines`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{\"name\":\"John\"}\n\n{\"name\":\"Jane\"}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        val successResult = result as ParseResult.Success
        assertEquals(2, successResult.jsonObjects.size)
    }

    @Test
    fun `parseNDJsonFile handles empty JSON object`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = "{}"
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Error)
        assertTrue((result as ParseResult.Error).message.contains("Empty JSON object"))
    }

    @Test
    fun `parseNDJsonFile handles more than 5 errors`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val ndjsonContent = (1..10).joinToString("\n") { "invalid$it" }
        val inputStream = ByteArrayInputStream(ndjsonContent.toByteArray())

        every { contentResolver.openInputStream(uri) } returns inputStream

        // When
        val result = repository.parseNDJsonFile(uri)

        // Then
        assertTrue(result is ParseResult.Error)
        val errorMessage = (result as ParseResult.Error).message
        assertTrue(errorMessage.contains("... and"))
        assertTrue(errorMessage.contains("more errors"))
    }
}


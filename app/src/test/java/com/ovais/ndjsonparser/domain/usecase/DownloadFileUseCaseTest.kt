package com.ovais.ndjsonparser.domain.usecase

import android.net.Uri
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DownloadFileUseCaseTest {

    private lateinit var repository: NDJsonRepository
    private lateinit var convertToJsonUseCase: ConvertToJsonUseCase
    private lateinit var convertToCsvUseCase: ConvertToCsvUseCase
    private lateinit var useCase: DownloadFileUseCase

    @Before
    fun setup() {
        repository = mockk()
        convertToJsonUseCase = mockk()
        convertToCsvUseCase = mockk()
        useCase = DownloadFileUseCase(repository, convertToJsonUseCase, convertToCsvUseCase)
    }

    @Test
    fun `invoke converts to JSON and saves file successfully`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "")
        )
        val jsonContent = "[{\"name\":\"John\",\"age\":30}]"
        val fileName = "test.json"
        val uri = mockk<Uri>()

        coEvery { convertToJsonUseCase(jsonObjects) } returns jsonContent
        coEvery { repository.saveFile(jsonContent, fileName, FileFormat.JSON) } returns Result.success(uri)

        // When
        val result = useCase(jsonObjects, FileFormat.JSON, fileName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(uri, result.getOrNull())
        coVerify { convertToJsonUseCase(jsonObjects) }
        coVerify { repository.saveFile(jsonContent, fileName, FileFormat.JSON) }
        coVerify(exactly = 0) { convertToCsvUseCase(any()) }
    }

    @Test
    fun `invoke converts to CSV and saves file successfully`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "")
        )
        val csvContent = "\"age\",\"name\"\n\"30\",\"John\"\n"
        val fileName = "test.csv"
        val uri = mockk<Uri>()

        coEvery { convertToCsvUseCase(jsonObjects) } returns csvContent
        coEvery { repository.saveFile(csvContent, fileName, FileFormat.CSV) } returns Result.success(uri)

        // When
        val result = useCase(jsonObjects, FileFormat.CSV, fileName)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(uri, result.getOrNull())
        coVerify { convertToCsvUseCase(jsonObjects) }
        coVerify { repository.saveFile(csvContent, fileName, FileFormat.CSV) }
        coVerify(exactly = 0) { convertToJsonUseCase(any()) }
    }

    @Test
    fun `invoke returns failure when saveFile fails`() = runTest {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val jsonContent = "[{\"name\":\"John\"}]"
        val fileName = "test.json"
        val exception = Exception("Save failed")

        coEvery { convertToJsonUseCase(jsonObjects) } returns jsonContent
        coEvery { repository.saveFile(jsonContent, fileName, FileFormat.JSON) } returns Result.failure(exception)

        // When
        val result = useCase(jsonObjects, FileFormat.JSON, fileName)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `invoke handles empty list for JSON`() = runTest {
        // Given
        val jsonObjects = emptyList<JsonObject>()
        val jsonContent = "[]"
        val fileName = "empty.json"
        val uri = mockk<Uri>()

        coEvery { convertToJsonUseCase(jsonObjects) } returns jsonContent
        coEvery { repository.saveFile(jsonContent, fileName, FileFormat.JSON) } returns Result.success(uri)

        // When
        val result = useCase(jsonObjects, FileFormat.JSON, fileName)

        // Then
        assertTrue(result.isSuccess)
        coVerify { convertToJsonUseCase(jsonObjects) }
    }

    @Test
    fun `invoke handles empty list for CSV`() = runTest {
        // Given
        val jsonObjects = emptyList<JsonObject>()
        val csvContent = ""
        val fileName = "empty.csv"
        val uri = mockk<Uri>()

        coEvery { convertToCsvUseCase(jsonObjects) } returns csvContent
        coEvery { repository.saveFile(csvContent, fileName, FileFormat.CSV) } returns Result.success(uri)

        // When
        val result = useCase(jsonObjects, FileFormat.CSV, fileName)

        // Then
        assertTrue(result.isSuccess)
        coVerify { convertToCsvUseCase(jsonObjects) }
    }
}


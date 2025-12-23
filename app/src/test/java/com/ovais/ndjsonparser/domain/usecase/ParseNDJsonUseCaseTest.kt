package com.ovais.ndjsonparser.domain.usecase

import android.net.Uri
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.model.ParseResult
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ParseNDJsonUseCaseTest {

    private lateinit var repository: NDJsonRepository
    private lateinit var useCase: ParseNDJsonUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ParseNDJsonUseCase(repository)
    }

    @Test
    fun `invoke returns success when repository parses successfully`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "{\"name\":\"John\",\"age\":30}"),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "{\"name\":\"Jane\",\"age\":25}")
        )
        val expectedResult = ParseResult.Success(jsonObjects)

        coEvery { repository.parseNDJsonFile(uri) } returns expectedResult

        // When
        val result = useCase(uri)

        // Then
        assertTrue(result is ParseResult.Success)
        assertEquals(jsonObjects, (result as ParseResult.Success).jsonObjects)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val uri = mockk<Uri>()
        val errorMessage = "File not found"
        val expectedResult = ParseResult.Error(errorMessage)

        coEvery { repository.parseNDJsonFile(uri) } returns expectedResult

        // When
        val result = useCase(uri)

        // Then
        assertTrue(result is ParseResult.Error)
        assertEquals(errorMessage, (result as ParseResult.Error).message)
    }
}


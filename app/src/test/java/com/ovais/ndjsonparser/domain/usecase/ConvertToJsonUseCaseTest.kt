package com.ovais.ndjsonparser.domain.usecase

import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConvertToJsonUseCaseTest {

    private lateinit var repository: NDJsonRepository
    private lateinit var useCase: ConvertToJsonUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ConvertToJsonUseCase(repository)
    }

    @Test
    fun `invoke converts json objects to json string`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )
        val expectedJson = "[{\"name\":\"John\",\"age\":30},{\"name\":\"Jane\",\"age\":25}]"

        coEvery { repository.convertToJson(jsonObjects) } returns expectedJson

        // When
        val result = useCase(jsonObjects)

        // Then
        assertEquals(expectedJson, result)
    }

    @Test
    fun `invoke returns empty array for empty list`() = runTest {
        // Given
        val jsonObjects = emptyList<JsonObject>()
        val expectedJson = "[]"

        coEvery { repository.convertToJson(jsonObjects) } returns expectedJson

        // When
        val result = useCase(jsonObjects)

        // Then
        assertEquals(expectedJson, result)
    }
}


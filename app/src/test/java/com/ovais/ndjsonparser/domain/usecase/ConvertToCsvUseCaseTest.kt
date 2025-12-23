package com.ovais.ndjsonparser.domain.usecase

import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ConvertToCsvUseCaseTest {

    private lateinit var repository: NDJsonRepository
    private lateinit var useCase: ConvertToCsvUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ConvertToCsvUseCase(repository)
    }

    @Test
    fun `invoke converts json objects to csv string`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )
        val expectedCsv = "\"age\",\"name\"\n\"30\",\"John\"\n\"25\",\"Jane\"\n"

        coEvery { repository.convertToCsv(jsonObjects) } returns expectedCsv

        // When
        val result = useCase(jsonObjects)

        // Then
        assertEquals(expectedCsv, result)
    }
}


package com.ovais.ndjsonparser.domain.usecase

import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FilterByKeyUseCaseTest {

    private lateinit var repository: NDJsonRepository
    private lateinit var useCase: FilterByKeyUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = FilterByKeyUseCase(repository)
    }

    @Test
    fun `invoke filters by key when value is provided`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), ""),
            JsonObject(mapOf("name" to "John", "age" to 35), "")
        )
        val key = "name"
        val value = "John"
        val expectedFiltered = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "John", "age" to 35), "")
        )

        coEvery { repository.filterByKey(jsonObjects, key, value) } returns expectedFiltered

        // When
        val result = useCase(jsonObjects, key, value)

        // Then
        assertEquals(expectedFiltered, result)
    }

    @Test
    fun `invoke filters by key existence when value is null`() = runTest {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("age" to 25), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 35), "")
        )
        val key = "name"
        val value: String? = null
        val expectedFiltered = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 35), "")
        )

        coEvery { repository.filterByKey(jsonObjects, key, value) } returns expectedFiltered

        // When
        val result = useCase(jsonObjects, key, value)

        // Then
        assertEquals(expectedFiltered, result)
    }
}


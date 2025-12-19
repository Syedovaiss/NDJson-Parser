package com.ovais.ndjsonparser.domain.usecase

import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository

/**
 * Use case for filtering JSON objects by key
 * Follows Single Responsibility Principle (SOLID)
 */
class FilterByKeyUseCase(
    private val repository: NDJsonRepository
) {
    suspend operator fun invoke(
        jsonObjects: List<JsonObject>,
        key: String,
        value: String?
    ): List<JsonObject> {
        return repository.filterByKey(jsonObjects, key, value)
    }
}


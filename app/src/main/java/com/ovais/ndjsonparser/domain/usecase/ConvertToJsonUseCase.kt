package com.ovais.ndjsonparser.domain.usecase

import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository

/**
 * Use case for converting parsed objects to JSON format
 * Follows Single Responsibility Principle (SOLID)
 */
class ConvertToJsonUseCase(
    private val repository: NDJsonRepository
) {
    suspend operator fun invoke(jsonObjects: List<JsonObject>): String {
        return repository.convertToJson(jsonObjects)
    }
}


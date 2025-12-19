package com.ovais.ndjsonparser.domain.usecase

import android.net.Uri
import com.ovais.ndjsonparser.domain.model.ParseResult
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository

/**
 * Use case for parsing NDJSON files
 * Follows Single Responsibility Principle (SOLID)
 */
class ParseNDJsonUseCase(
    private val repository: NDJsonRepository
) {
    suspend operator fun invoke(uri: Uri): ParseResult {
        return repository.parseNDJsonFile(uri)
    }
}


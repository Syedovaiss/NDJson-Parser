package com.ovais.ndjsonparser.domain.usecase

import android.net.Uri
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository

/**
 * Use case for downloading converted files
 * Follows Single Responsibility Principle (SOLID)
 */
class DownloadFileUseCase(
    private val repository: NDJsonRepository,
    private val convertToJsonUseCase: ConvertToJsonUseCase,
    private val convertToCsvUseCase: ConvertToCsvUseCase
) {
    suspend operator fun invoke(
        jsonObjects: List<JsonObject>,
        format: FileFormat
    ): Result<Uri> {
        val content = when (format) {
            FileFormat.JSON -> convertToJsonUseCase(jsonObjects)
            FileFormat.CSV -> convertToCsvUseCase(jsonObjects)
        }
        val fileName = "ndjson_export.${format.name.lowercase()}"
        return repository.saveFile(content, fileName, format)
    }
}


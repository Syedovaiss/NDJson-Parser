package com.ovais.ndjsonparser.domain.model

/**
 * Result of parsing NDJSON file
 */
sealed class ParseResult {
    data class Success(val jsonObjects: List<JsonObject>) : ParseResult()
    data class Error(val message: String) : ParseResult()
}


package com.ovais.ndjsonparser.domain.model

/**
 * Represents a parsed JSON object from NDJSON file
 */
data class JsonObject(
    val data: Map<String, Any?>,
    val originalLine: String
)


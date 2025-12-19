package com.ovais.ndjsonparser.presentation.ui.state

import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject

/**
 * UI state for NDJSON Parser screen
 */
data class NDJsonUiState(
    val isLoading: Boolean = false,
    val jsonObjects: List<JsonObject> = emptyList(),
    val filteredObjects: List<JsonObject> = emptyList(),
    val errorMessage: String? = null,
    val filterKey: String = "",
    val filterValue: String = "",
    val isFilterActive: Boolean = false,
    val selectedFileUri: android.net.Uri? = null,
    val downloadSuccess: Boolean = false,
    val downloadError: String? = null
) {
    val displayObjects: List<JsonObject>
        get() = if (isFilterActive) filteredObjects else jsonObjects
    
    val hasData: Boolean
        get() = jsonObjects.isNotEmpty()
}


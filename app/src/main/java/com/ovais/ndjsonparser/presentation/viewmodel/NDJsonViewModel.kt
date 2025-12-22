package com.ovais.ndjsonparser.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.ParseResult
import com.ovais.ndjsonparser.domain.usecase.ConvertToCsvUseCase
import com.ovais.ndjsonparser.domain.usecase.ConvertToJsonUseCase
import com.ovais.ndjsonparser.domain.usecase.DownloadFileUseCase
import com.ovais.ndjsonparser.domain.usecase.FilterByKeyUseCase
import com.ovais.ndjsonparser.domain.usecase.ParseNDJsonUseCase
import com.ovais.ndjsonparser.presentation.ui.state.NDJsonUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for NDJSON Parser
 * Follows MVVM pattern and Single Responsibility Principle (SOLID)
 */
class NDJsonViewModel(
    private val parseNDJsonUseCase: ParseNDJsonUseCase,
    private val convertToJsonUseCase: ConvertToJsonUseCase,
    private val convertToCsvUseCase: ConvertToCsvUseCase,
    private val filterByKeyUseCase: FilterByKeyUseCase,
    private val downloadFileUseCase: DownloadFileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NDJsonUiState())
    val uiState: StateFlow<NDJsonUiState> = _uiState.asStateFlow()

    fun parseFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            when (val result = parseNDJsonUseCase(uri)) {
                is ParseResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            jsonObjects = result.jsonObjects,
                            filteredObjects = result.jsonObjects,
                            selectedFileUri = uri,
                            errorMessage = null
                        )
                    }
                }
                is ParseResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message,
                            jsonObjects = emptyList(),
                            filteredObjects = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun applyFilter(key: String, value: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val trimmedKey = key.trim()
            val trimmedValue = value.trim()
            
            _uiState.update { 
                it.copy(
                    isLoading = true, 
                    filterKey = trimmedKey, 
                    filterValue = trimmedValue
                ) 
            }
            
            // Use the original jsonObjects from state, not the filtered ones
            val filtered = filterByKeyUseCase(currentState.jsonObjects, trimmedKey, trimmedValue)
            
            _uiState.update {
                it.copy(
                    isLoading = false,
                    filteredObjects = filtered,
                    isFilterActive = trimmedKey.isNotBlank(),
                    errorMessage = null
                )
            }
        }
    }

    fun clearFilter() {
        _uiState.update {
            it.copy(
                filterKey = "",
                filterValue = "",
                isFilterActive = false,
                filteredObjects = it.jsonObjects
            )
        }
    }

    fun showFileNameDialog(format: FileFormat) {
        val objectsToExport = _uiState.value.displayObjects
        if (objectsToExport.isEmpty()) {
            _uiState.update {
                it.copy(downloadError = "No data to export")
            }
            return
        }

        _uiState.update {
            it.copy(
                showFileNameDialog = true,
                pendingDownloadFormat = format
            )
        }
    }

    fun dismissFileNameDialog() {
        _uiState.update {
            it.copy(
                showFileNameDialog = false,
                pendingDownloadFormat = null
            )
        }
    }

    fun downloadFile(fileName: String) {
        viewModelScope.launch {
            val format = _uiState.value.pendingDownloadFormat ?: return@launch
            val objectsToExport = _uiState.value.displayObjects

            _uiState.update {
                it.copy(
                    isLoading = true,
                    downloadError = null,
                    downloadSuccess = false,
                    showFileNameDialog = false,
                    pendingDownloadFormat = null
                )
            }

            downloadFileUseCase(objectsToExport, format, fileName)
                .onSuccess { uri ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            downloadSuccess = true,
                            downloadError = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            downloadError = exception.message ?: "Failed to download file",
                            downloadSuccess = false
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, downloadError = null) }
    }

    fun clearDownloadSuccess() {
        _uiState.update { it.copy(downloadSuccess = false) }
    }
}


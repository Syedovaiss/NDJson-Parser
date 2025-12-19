package com.ovais.ndjsonparser.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ovais.ndjsonparser.R
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.presentation.ui.components.DataDisplay
import com.ovais.ndjsonparser.presentation.ui.components.DownloadButtons
import com.ovais.ndjsonparser.presentation.ui.components.FilePickerButton
import com.ovais.ndjsonparser.presentation.ui.components.FilterSection
import com.ovais.ndjsonparser.presentation.viewmodel.NDJsonViewModel
import com.ovais.ndjsonparser.ui.theme.Primary
import com.ovais.ndjsonparser.ui.theme.Secondary

@Composable
fun NDJsonParserScreen(
    viewModel: NDJsonViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.downloadSuccess) {
        if (uiState.downloadSuccess) {
            snackbarHostState.showSnackbar("File downloaded successfully!")
            viewModel.clearDownloadSuccess()
        }
    }

    LaunchedEffect(uiState.downloadError) {
        uiState.downloadError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_parser),
                        contentDescription = null,
                        modifier = Modifier
                            .height(48.dp)
                            .padding(bottom = 8.dp),
                    )
                    Text(
                        text = "NDJSON Parser",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Parse, filter, and convert NDJSON files",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // File Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                FilePickerButton(
                    onFileSelected = { uri ->
                        viewModel.parseFile(uri)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading Indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Content Section
            if (uiState.hasData) {
                // Filter Section
                FilterSection(
                    filterKey = uiState.filterKey,
                    filterValue = uiState.filterValue,
                    isFilterActive = uiState.isFilterActive,
                    onFilterApply = { key, value ->
                        viewModel.applyFilter(key, value)
                    },
                    onFilterClear = {
                        viewModel.clearFilter()
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Download Buttons
                DownloadButtons(
                    onDownloadJson = {
                        viewModel.downloadFile(FileFormat.JSON)
                    },
                    onDownloadCsv = {
                        viewModel.downloadFile(FileFormat.CSV)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Data Display
                DataDisplay(
                    jsonObjects = uiState.displayObjects,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                )
            } else if (!uiState.isLoading && uiState.errorMessage == null) {
                // Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.height(48.dp),
                            tint = Primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No file selected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Select an NDJSON file to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

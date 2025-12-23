package com.ovais.ndjsonparser.presentation.viewmodel

import android.net.Uri
import app.cash.turbine.test
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.domain.model.ParseResult
import com.ovais.ndjsonparser.domain.usecase.ConvertToCsvUseCase
import com.ovais.ndjsonparser.domain.usecase.ConvertToJsonUseCase
import com.ovais.ndjsonparser.domain.usecase.DownloadFileUseCase
import com.ovais.ndjsonparser.domain.usecase.FilterByKeyUseCase
import com.ovais.ndjsonparser.domain.usecase.ParseNDJsonUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NDJsonViewModelTest {

    private lateinit var parseNDJsonUseCase: ParseNDJsonUseCase
    private lateinit var convertToJsonUseCase: ConvertToJsonUseCase
    private lateinit var convertToCsvUseCase: ConvertToCsvUseCase
    private lateinit var filterByKeyUseCase: FilterByKeyUseCase
    private lateinit var downloadFileUseCase: DownloadFileUseCase
    private lateinit var viewModel: NDJsonViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        parseNDJsonUseCase = mockk()
        convertToJsonUseCase = mockk()
        convertToCsvUseCase = mockk()
        filterByKeyUseCase = mockk()
        downloadFileUseCase = mockk()
        viewModel = NDJsonViewModel(
            parseNDJsonUseCase,
            convertToJsonUseCase,
            convertToCsvUseCase,
            filterByKeyUseCase,
            downloadFileUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `parseFile updates state with success result`() = runTest(testDispatcher) {
        // Given
        val uri = mockk<Uri>()
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), "")
        )
        val result = ParseResult.Success(jsonObjects)

        coEvery { parseNDJsonUseCase(uri) } returns result

        // When
        viewModel.parseFile(uri)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(jsonObjects, state.jsonObjects)
        assertEquals(jsonObjects, state.filteredObjects)
        assertEquals(uri, state.selectedFileUri)
        assertTrue(state.errorMessage == null)
    }

    @Test
    fun `parseFile updates state with error result`() = runTest(testDispatcher) {
        // Given
        val uri = mockk<Uri>()
        val errorMessage = "Parse error"
        val result = ParseResult.Error(errorMessage)

        coEvery { parseNDJsonUseCase(uri) } returns result

        // When
        viewModel.parseFile(uri)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(errorMessage, state.errorMessage)
        assertTrue(state.jsonObjects.isEmpty())
        assertTrue(state.filteredObjects.isEmpty())
    }

    @Test
    fun `applyFilter filters data correctly`() = runTest(testDispatcher) {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), ""),
            JsonObject(mapOf("name" to "Jane", "age" to 25), "")
        )
        val filtered = listOf(JsonObject(mapOf("name" to "John", "age" to 30), ""))
        
        // Set initial data
        val uri = mockk<Uri>()
        coEvery { parseNDJsonUseCase(uri) } returns ParseResult.Success(jsonObjects)
        viewModel.parseFile(uri)
        advanceUntilIdle()
        
        // Mock the filter
        coEvery { filterByKeyUseCase(jsonObjects, "name", "John") } returns filtered

        // When
        viewModel.applyFilter("name", "John")
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("name", state.filterKey)
        assertEquals("John", state.filterValue)
        assertTrue(state.isFilterActive)
        assertEquals(filtered, state.filteredObjects)
    }

    @Test
    fun `clearFilter resets filter state`() = runTest(testDispatcher) {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val uri = mockk<Uri>()
        coEvery { parseNDJsonUseCase(uri) } returns ParseResult.Success(jsonObjects)
        coEvery { filterByKeyUseCase(any(), any(), any()) } returns jsonObjects
        
        // Set initial data and filter
        viewModel.parseFile(uri)
        advanceUntilIdle()
        viewModel.applyFilter("name", "John")
        advanceUntilIdle()
        
        // When
        viewModel.clearFilter()

        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.filterKey)
        assertEquals("", state.filterValue)
        assertFalse(state.isFilterActive)
        assertEquals(jsonObjects, state.filteredObjects)
    }

    @Test
    fun `showFileNameDialog shows dialog when data exists`() = runTest(testDispatcher) {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val uri = mockk<Uri>()
        coEvery { parseNDJsonUseCase(uri) } returns ParseResult.Success(jsonObjects)
        viewModel.parseFile(uri)
        advanceUntilIdle()

        // When
        viewModel.showFileNameDialog(FileFormat.JSON)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.showFileNameDialog)
        assertEquals(FileFormat.JSON, state.pendingDownloadFormat)
    }

    @Test
    fun `showFileNameDialog shows error when no data`() = runTest {
        // When
        viewModel.showFileNameDialog(FileFormat.JSON)

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.showFileNameDialog)
        assertEquals("No data to export", state.downloadError)
    }

    @Test
    fun `downloadFile downloads successfully`() = runTest(testDispatcher) {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val uri = mockk<Uri>()
        val parseUri = mockk<Uri>()
        coEvery { parseNDJsonUseCase(parseUri) } returns ParseResult.Success(jsonObjects)
        coEvery { downloadFileUseCase(any(), FileFormat.JSON, "test.json") } returns Result.success(uri)
        
        viewModel.parseFile(parseUri)
        advanceUntilIdle()
        viewModel.showFileNameDialog(FileFormat.JSON)
        advanceUntilIdle()

        // When
        viewModel.downloadFile("test.json")
        advanceUntilIdle()

        // Then
        coVerify { downloadFileUseCase(jsonObjects, FileFormat.JSON, "test.json") }
        val state = viewModel.uiState.value
        assertTrue(state.downloadSuccess)
        assertFalse(state.isLoading)
    }
}


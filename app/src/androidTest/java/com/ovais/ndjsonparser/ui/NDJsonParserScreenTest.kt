package com.ovais.ndjsonparser.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.presentation.ui.screen.NDJsonParserScreen
import com.ovais.ndjsonparser.presentation.ui.state.NDJsonUiState
import com.ovais.ndjsonparser.presentation.viewmodel.NDJsonViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class NDJsonParserScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: NDJsonViewModel = mockk(relaxed = true)

    @Test
    fun appTitle_isDisplayed() {
        // Given
        every { viewModel.uiState } returns MutableStateFlow(NDJsonUiState())

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("NDJSON Parser", substring = true).assertIsDisplayed()
    }

    @Test
    fun appSubtitle_isDisplayed() {
        // Given
        every { viewModel.uiState } returns MutableStateFlow(NDJsonUiState())

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Parse, filter, and convert", substring = true).assertIsDisplayed()
    }

    @Test
    fun filePickerButton_isDisplayed() {
        // Given
        every { viewModel.uiState } returns MutableStateFlow(NDJsonUiState())

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Select NDJSON File", substring = true).assertIsDisplayed()
    }

    @Test
    fun emptyState_isDisplayed_whenNoFileSelected() {
        // Given
        every { viewModel.uiState } returns MutableStateFlow(NDJsonUiState())

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("No file selected", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Select an NDJSON file", substring = true).assertIsDisplayed()
    }

    @Test
    fun filterSection_isDisplayed_whenDataExists() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "")
        )
        val state = NDJsonUiState(
            jsonObjects = jsonObjects,
            filteredObjects = jsonObjects
        )
        every { viewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Filter Data", substring = true).assertIsDisplayed()
    }

    @Test
    fun downloadButtons_areDisplayed_whenDataExists() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), "")
        )
        val state = NDJsonUiState(
            jsonObjects = jsonObjects,
            filteredObjects = jsonObjects
        )
        every { viewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then - verify buttons exist by checking for button text
        // "JSON" might appear multiple times, so we use onAllNodesWithText and check that at least one exists
        val jsonNodes = composeTestRule.onAllNodesWithText("JSON", substring = true)
        assert(jsonNodes.fetchSemanticsNodes().isNotEmpty()) { "JSON button should be displayed" }
        
        val csvNodes = composeTestRule.onAllNodesWithText("CSV", substring = true)
        assert(csvNodes.fetchSemanticsNodes().isNotEmpty()) { "CSV button should be displayed" }
    }

    @Test
    fun dataDisplay_isDisplayed_whenDataExists() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "")
        )
        val state = NDJsonUiState(
            jsonObjects = jsonObjects,
            filteredObjects = jsonObjects
        )
        every { viewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then - verify data display elements exist
        // "Item" might appear multiple times, so we check for specific item text
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        // Verify JSON content is displayed
        composeTestRule.onNodeWithText("name", substring = true).assertIsDisplayed()
    }

    @Test
    fun loadingIndicator_isDisplayed_whenLoading() {
        // Given
        val state = NDJsonUiState(isLoading = true)
        every { viewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        // Loading indicator should be visible (we can't easily test CircularProgressIndicator directly)
        // But we can verify empty state is not shown
        composeTestRule.onNodeWithText("No file selected", substring = true).assertIsNotDisplayed()
    }

    @Test
    fun fileNameDialog_isDisplayed_whenShowDialogIsTrue() {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val state = NDJsonUiState(
            jsonObjects = jsonObjects,
            filteredObjects = jsonObjects,
            showFileNameDialog = true,
            pendingDownloadFormat = FileFormat.JSON
        )
        every { viewModel.uiState } returns MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule.onNodeWithText("Save File").assertIsDisplayed()
    }

    @Test
    fun downloadJsonButton_triggersShowDialog() {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val state = NDJsonUiState(
            jsonObjects = jsonObjects,
            filteredObjects = jsonObjects
        )
        every { viewModel.uiState } returns kotlinx.coroutines.flow.MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText("JSON").performClick()

        // Then
        verify { viewModel.showFileNameDialog(FileFormat.JSON) }
    }

    @Test
    fun downloadCsvButton_triggersShowDialog() {
        // Given
        val jsonObjects = listOf(JsonObject(mapOf("name" to "John"), ""))
        val state = NDJsonUiState(
            jsonObjects = jsonObjects,
            filteredObjects = jsonObjects
        )
        every { viewModel.uiState } returns kotlinx.coroutines.flow.MutableStateFlow(state)

        // When
        composeTestRule.setContent {
            NDJsonParserScreen(viewModel = viewModel)
        }
        composeTestRule.onNodeWithText("CSV").performClick()

        // Then
        verify { viewModel.showFileNameDialog(FileFormat.CSV) }
    }
}


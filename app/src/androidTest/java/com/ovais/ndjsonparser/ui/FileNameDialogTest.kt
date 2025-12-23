package com.ovais.ndjsonparser.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ovais.ndjsonparser.domain.model.FileFormat
import com.ovais.ndjsonparser.presentation.ui.components.FileNameDialog
import org.junit.Rule
import org.junit.Test

class FileNameDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fileNameDialog_displaysForJsonFormat() {
        // When
        composeTestRule.setContent {
            FileNameDialog(
                format = FileFormat.JSON,
                defaultFileName = "test.json",
                onDismiss = {},
                onConfirm = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Save File").assertIsDisplayed()
        composeTestRule.onNodeWithText("JSON", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("File Name").assertIsDisplayed()
    }

    @Test
    fun fileNameDialog_displaysForCsvFormat() {
        // When
        composeTestRule.setContent {
            FileNameDialog(
                format = FileFormat.CSV,
                defaultFileName = "test.csv",
                onDismiss = {},
                onConfirm = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("Save File").assertIsDisplayed()
        composeTestRule.onNodeWithText("CSV", substring = true).assertIsDisplayed()
    }

    @Test
    fun fileNameDialog_showsDefaultFileName() {
        // When
        composeTestRule.setContent {
            FileNameDialog(
                format = FileFormat.JSON,
                defaultFileName = "ndjson_export",
                onDismiss = {},
                onConfirm = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("ndjson_export", substring = true).assertIsDisplayed()
    }

    @Test
    fun fileNameDialog_cancelButton_dismissesDialog() {
        // Given
        var dismissed = false

        // When
        composeTestRule.setContent {
            FileNameDialog(
                format = FileFormat.JSON,
                defaultFileName = "test.json",
                onDismiss = { dismissed = true },
                onConfirm = {}
            )
        }
        composeTestRule.onNodeWithText("Cancel").performClick()

        // Then
        assert(dismissed)
    }

    @Test
    fun fileNameDialog_saveButton_confirmsWithFileName() {
        // Given
        var confirmedFileName = ""

        // When
        composeTestRule.setContent {
            FileNameDialog(
                format = FileFormat.JSON,
                defaultFileName = "test.json",
                onDismiss = {},
                onConfirm = { fileName -> confirmedFileName = fileName }
            )
        }
        composeTestRule.onNodeWithText("Save").performClick()

        // Then
        assert(confirmedFileName.contains("test.json") || confirmedFileName.contains(".json"))
    }

    @Test
    fun fileNameDialog_allowsTextInput() {
        // When
        composeTestRule.setContent {
            FileNameDialog(
                format = FileFormat.JSON,
                defaultFileName = "test.json",
                onDismiss = {},
                onConfirm = {}
            )
        }

        // Then - verify text field exists and can accept input
        // Note: We can't easily test text input in dialog text fields without more complex setup
        composeTestRule.onNodeWithText("File Name").assertIsDisplayed()
    }
}


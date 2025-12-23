package com.ovais.ndjsonparser.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ovais.ndjsonparser.presentation.ui.components.DownloadButtons
import org.junit.Rule
import org.junit.Test

class DownloadButtonsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun downloadButtons_displayBothButtons() {
        // When
        composeTestRule.setContent {
            DownloadButtons(
                onDownloadJson = {},
                onDownloadCsv = {}
            )
        }

        // Then
        composeTestRule.onNodeWithText("JSON").assertIsDisplayed()
        composeTestRule.onNodeWithText("CSV").assertIsDisplayed()
    }

    @Test
    fun downloadJsonButton_triggersCallback() {
        // Given
        var jsonClicked = false

        // When
        composeTestRule.setContent {
            DownloadButtons(
                onDownloadJson = { jsonClicked = true },
                onDownloadCsv = {}
            )
        }
        composeTestRule.onNodeWithText("JSON").performClick()

        // Then
        assert(jsonClicked)
    }

    @Test
    fun downloadCsvButton_triggersCallback() {
        // Given
        var csvClicked = false

        // When
        composeTestRule.setContent {
            DownloadButtons(
                onDownloadJson = {},
                onDownloadCsv = { csvClicked = true }
            )
        }
        composeTestRule.onNodeWithText("CSV").performClick()

        // Then
        assert(csvClicked)
    }
}


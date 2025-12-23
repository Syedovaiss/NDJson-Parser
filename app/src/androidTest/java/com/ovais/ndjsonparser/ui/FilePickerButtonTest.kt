package com.ovais.ndjsonparser.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ovais.ndjsonparser.presentation.ui.components.FilePickerButton
import org.junit.Rule
import org.junit.Test

class FilePickerButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun filePickerButton_displaysCorrectly() {
        // When
        composeTestRule.setContent {
            FilePickerButton(onFileSelected = {})
        }

        // Then
        composeTestRule.onNodeWithText("Select NDJSON File").assertIsDisplayed()
    }

    @Test
    fun filePickerButton_isClickable() {
        // When
        composeTestRule.setContent {
            FilePickerButton(onFileSelected = {})
        }

        // Then
        val button = composeTestRule.onNodeWithText("Select NDJSON File")
        button.assertIsDisplayed()
        // Note: We can't easily test the file picker launcher without mocking ActivityResultLauncher
        // But we can verify the button is present and clickable
        button.performClick()
    }
}


package com.ovais.ndjsonparser.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ovais.ndjsonparser.NDJsonParserActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NDJsonParserActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NDJsonParserActivity>()

    @Test
    fun activity_displaysMainScreen() {
        // When activity is launched
        composeTestRule.activityRule.scenario.onActivity { }

        // Then
        composeTestRule.onNodeWithText("NDJSON Parser", substring = true).assertIsDisplayed()
    }

    @Test
    fun activity_displaysFilePickerButton() {
        // When activity is launched
        composeTestRule.activityRule.scenario.onActivity { }

        // Then
        composeTestRule.onNodeWithText("Select NDJSON File", substring = true).assertIsDisplayed()
    }

    @Test
    fun activity_displaysEmptyState() {
        // When activity is launched
        composeTestRule.activityRule.scenario.onActivity { }

        // Then
        composeTestRule.onNodeWithText("No file selected", substring = true).assertIsDisplayed()
    }
}


package com.ovais.ndjsonparser.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.ovais.ndjsonparser.domain.model.JsonObject
import com.ovais.ndjsonparser.presentation.ui.components.DataDisplay
import org.junit.Rule
import org.junit.Test

class DataDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dataDisplay_showsEmptyState_whenListIsEmpty() {
        // When
        composeTestRule.setContent {
            DataDisplay(jsonObjects = emptyList())
        }

        // Then
        composeTestRule.onNodeWithText("No data to display").assertIsDisplayed()
    }

    @Test
    fun dataDisplay_showsItemCount_whenDataExists() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "")
        )

        // When
        composeTestRule.setContent {
            DataDisplay(jsonObjects = jsonObjects)
        }

        // Then - use more specific text matching
        composeTestRule.onNodeWithText("1 Item", substring = true).assertIsDisplayed()
    }

    @Test
    fun dataDisplay_showsMultipleItems() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John"), ""),
            JsonObject(mapOf("name" to "Jane"), "")
        )

        // When
        composeTestRule.setContent {
            DataDisplay(jsonObjects = jsonObjects)
        }

        // Then - use more specific text matching to avoid multiple matches
        composeTestRule.onNodeWithText("2 Items", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    @Test
    fun dataDisplay_showsJsonContent() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30), "")
        )

        // When
        composeTestRule.setContent {
            DataDisplay(jsonObjects = jsonObjects)
        }

        // Then
        composeTestRule.onNodeWithText("name", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("John", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("age", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("30", substring = true).assertIsDisplayed()
    }

    @Test
    fun dataDisplay_showsFieldsCount() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "age" to 30, "city" to "NYC"), "")
        )

        // When
        composeTestRule.setContent {
            DataDisplay(jsonObjects = jsonObjects)
        }

        // Then
        composeTestRule.onNodeWithText("3 fields", substring = true).assertIsDisplayed()
    }

    @Test
    fun dataDisplay_handlesNullValues() {
        // Given
        val jsonObjects = listOf(
            JsonObject(mapOf("name" to "John", "middleName" to null), "")
        )

        // When
        composeTestRule.setContent {
            DataDisplay(jsonObjects = jsonObjects)
        }

        // Then
        composeTestRule.onNodeWithText("name", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("null", substring = true).assertIsDisplayed()
    }
}


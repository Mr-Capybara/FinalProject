package com.example.finalproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ClarityNavigationTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun bottomNavigationShowsChineseTabs() {
        listOf("日历", "任务", "统计", "专注", "设置").forEach {
            composeRule.onNodeWithText(it).assertIsDisplayed()
        }
    }

    @Test
    fun canSwitchBetweenMainScreens() {
        composeRule.onNodeWithText("任务").performClick()
        composeRule.onNodeWithText("全部任务").assertIsDisplayed()

        composeRule.onNodeWithText("统计").performClick()
        composeRule.onNodeWithText("专注时长").assertIsDisplayed()

        composeRule.onNodeWithText("设置").performClick()
        composeRule.onNodeWithText("外观").assertIsDisplayed()
    }
}

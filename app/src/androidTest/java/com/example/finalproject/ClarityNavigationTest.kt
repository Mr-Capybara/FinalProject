package com.example.finalproject

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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
        composeRule.onNodeWithText("习惯养成").assertIsDisplayed()
        composeRule.onNodeWithText("平均时长").assertIsDisplayed()

        composeRule.onNodeWithText("专注").performClick()
        composeRule.onNodeWithText("倒计时").assertIsDisplayed()
        composeRule.onNodeWithText("正计时").assertIsDisplayed()

        composeRule.onNodeWithText("设置").performClick()
        composeRule.onNodeWithText("外观").assertIsDisplayed()
    }

    @Test
    fun editScreenShowsDateAndTimePickerEntrypoints() {
        composeRule.onNodeWithText("任务").performClick()
        composeRule.onNodeWithContentDescription("新增任务").performClick()

        composeRule.onNodeWithText("日期").assertIsDisplayed()
        composeRule.onNodeWithText("开始时间").assertIsDisplayed()
        composeRule.onNodeWithText("结束时间").assertIsDisplayed()
    }

    @Test
    fun settingsShowsConciseInstructions() {
        composeRule.onNodeWithText("设置").performClick()
        composeRule.onNodeWithText("使用说明").performClick()

        composeRule.onNodeWithText("时间可留空", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("倒计时或正计时", substring = true).assertIsDisplayed()
    }
}

package com.fake.soundremote

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // First screen is HomeScreen
    @Test
    fun firstScreen_isHomeScreen() {
        composeTestRule
            .onNodeWithText(composeTestRule.activity.getString(R.string.home_title))
            .assertIsDisplayed()
    }

    // HomeScreen navigation menu button opens navigation menu
    @Test
    fun navigationMenuButton_onClick_showsNavigationMenu() {
        composeTestRule.apply {
            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.navigation_menu)).performClick()
            onNodeWithContentDescription(composeTestRule.activity.getString(R.string.navigation_menu_description)).assertIsDisplayed()
            onNodeWithText(composeTestRule.activity.getString(R.string.action_events)).assertIsDisplayed()
            onNodeWithText(composeTestRule.activity.getString(R.string.action_settings)).assertIsDisplayed()
            onNodeWithText(composeTestRule.activity.getString(R.string.action_about)).assertIsDisplayed()
        }
    }
}

package com.fake.soundremote

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
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
import kotlin.properties.ReadOnlyProperty

@HiltAndroidTest
class NavigationTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
        ReadOnlyProperty<Any?, String> { _, _ -> activity.getString(resId) }

    private val appName by composeTestRule.stringResource(R.string.app_name)
    private val home by composeTestRule.stringResource(R.string.home_title)
    private val navigationMenu by composeTestRule.stringResource(R.string.navigation_menu)
    private val navigationMenuDropdown by composeTestRule.stringResource(R.string.navigation_menu_description)
    private val menuEvents by composeTestRule.stringResource(R.string.action_events)
    private val menuSettings by composeTestRule.stringResource(R.string.action_settings)
    private val menuAbout by composeTestRule.stringResource(R.string.action_about)
    private val eventsTitle by composeTestRule.stringResource(R.string.event_list_title)
    private val settingsTitle by composeTestRule.stringResource(R.string.settings_title)
    private val aboutTitleTemplate by composeTestRule.stringResource(R.string.about_title_template)

    // First screen is HomeScreen
    @Test
    fun firstScreen_isHomeScreen() {
        composeTestRule.onNodeWithText(home).assertIsDisplayed()
    }

    // HomeScreen navigation menu button opens navigation menu
    @Test
    fun navigationMenuButton_onClick_showsNavigationMenu() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithContentDescription(navigationMenuDropdown).assertIsDisplayed()
            onNodeWithText(menuEvents).assertIsDisplayed()
            onNodeWithText(menuSettings).assertIsDisplayed()
            onNodeWithText(menuAbout).assertIsDisplayed()
        }
    }

    // Menu Events navigates to events screen
    @Test
    fun menuEvents_onClick_navigatesToEventsScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithText(menuEvents).performClick()
            onNodeWithText(eventsTitle).assertIsDisplayed()
        }
    }

    // Menu Settings navigates to settings screen
    @Test
    fun menuSettings_onClick_navigatesToSettingsScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithText(menuSettings).performClick()
            onNodeWithText(settingsTitle).assertIsDisplayed()
        }
    }

    // Menu About navigates to about screen
    @Test
    fun menuAbout_onClick_navigatesToAboutScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithText(menuAbout).performClick()
            val screenTitle = aboutTitleTemplate.format(appName)
            onNodeWithText(screenTitle).assertIsDisplayed()
        }
    }
}

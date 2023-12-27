package com.fake.soundremote

import androidx.annotation.StringRes
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.SemanticsNodeInteraction
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

    // Screen titles
    private val homeTitle by composeTestRule.stringResource(R.string.home_title)
    private val eventsTitle by composeTestRule.stringResource(R.string.event_list_title)
    private val settingsTitle by composeTestRule.stringResource(R.string.settings_title)
    private val aboutTitleTemplate by composeTestRule.stringResource(R.string.about_title_template)
    private val keystrokesTitle by composeTestRule.stringResource(R.string.keystroke_list_title)
    private val createKeystrokeTitle by composeTestRule.stringResource(R.string.keystroke_create_title)

    private val navigationMenu by composeTestRule.stringResource(R.string.navigation_menu)
    private val navigationMenuDropdown by composeTestRule.stringResource(R.string.navigation_menu_description)
    private val menuEvents by composeTestRule.stringResource(R.string.action_events)
    private val menuSettings by composeTestRule.stringResource(R.string.action_settings)
    private val menuAbout by composeTestRule.stringResource(R.string.action_about)
    private val editKeystrokes by composeTestRule.stringResource(R.string.action_edit_keystrokes)
    private val createKeystroke by composeTestRule.stringResource(R.string.action_keystroke_create)


    // First screen is HomeScreen
    @Test
    fun firstScreen_isHomeScreen() {
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
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

    // Back from events screen returns to home screen
    @Test
    fun eventsScreen_back_returnsToHomeScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithText(menuEvents).performClick()
            onNodeWithText(eventsTitle).assertIsDisplayed()
        }
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
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

    // Back from settings screen returns to home screen
    @Test
    fun settingsScreen_back_returnsToHomeScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithText(menuSettings).performClick()
            onNodeWithText(settingsTitle).assertIsDisplayed()
        }
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
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

    // Back from about screen returns to home screen
    @Test
    fun aboutScreen_back_returnsToHomeScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(navigationMenu).performClick()
            onNodeWithText(menuAbout).performClick()
            val screenTitle = aboutTitleTemplate.format(appName)
            onNodeWithText(screenTitle).assertIsDisplayed()
        }
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
    }

    // Edit keystrokes FAB navigates to keystrokes screen
    @Test
    fun editKeystrokesFab_click_navigatesToHomeScreen() {
        composeTestRule.onNodeWithContentDescription(editKeystrokes).performClick()
        composeTestRule.onNodeWithText(keystrokesTitle).assertIsDisplayed()
    }

    // Back from keystrokes screen returns to home screen
    @Test
    fun keystrokesScreen_back_returnsToHomeScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(editKeystrokes).performClick()
            onNodeWithText(keystrokesTitle).assertIsDisplayed()
        }
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
    }

    // Given: simultaneous clicks on different navigation menu items on `Home` screen.
    // Clicking back on opened screen should return to `Home` screen.
    // I.e. app should not navigate to other screens more than once.
    @Test
    fun navigateMenu_clickTwoItemsSimultaneously_navigatesOnce() {
        composeTestRule.apply {
            // Open navigation menu
            onNodeWithContentDescription(navigationMenu).performClick()
            // Click 2 menu items simultaneously
            onNodeWithText(menuAbout).performClick()
            mainClock.autoAdvance = false
            onNodeWithText(menuSettings).performClick()
            mainClock.autoAdvance = true
        }
        // Press back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        // Should be on `Home` screen
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
    }

    // Given: multiple simultaneous clicks on `Edit keystrokes` FAB on `Home` screen.
    // Clicking back on `Keystrokes` screen should return to `Home` screen.
    // I.e. app should not navigate to `Keystrokes` screens more than once.
    @Test
    fun multipleClicksEditKeystrokes_back_returnsToHomeScreen() {
        composeTestRule.apply {
            // Imitate double click on `Edit keystrokes` button
            onNodeWithContentDescription(editKeystrokes)
                .performSimultaneousDoubleClick(composeTestRule.mainClock)
        }
        // Press back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        // Should be on `Home` screen
        composeTestRule.onNodeWithText(homeTitle).assertIsDisplayed()
    }

    // Create keystroke button navigates to create keystroke screen
    @Test
    fun createKeystroke_click_navigatesToCreateKeystrokeScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(editKeystrokes).performClick()
            onNodeWithContentDescription(createKeystroke).performClick()
        }
        composeTestRule.onNodeWithText(createKeystrokeTitle).assertIsDisplayed()
    }

    // Back from create keystroke screen returns to keystrokes screen
    @Test
    fun createKeystroke_back_returnsToKeystrokesScreen() {
        composeTestRule.apply {
            onNodeWithContentDescription(editKeystrokes).performClick()
            onNodeWithContentDescription(createKeystroke).performClick()
            onNodeWithText(createKeystrokeTitle).assertIsDisplayed()
        }
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText(keystrokesTitle).assertIsDisplayed()
    }

    // Given: multiple simultaneous clicks on `create keystroke` button on `Keystrokes` screen.
    // Clicking back on `Create keystroke` screen should return to `Keystrokes` screen
    // I.e. app should not navigate to `Create keystroke` screen more than once.
    @Test
    fun multipleClicksCreateKeystroke_back_returnsToKeystrokesScreen() {
        composeTestRule.apply {
            // Go to `Keystrokes` screen
            onNodeWithContentDescription(editKeystrokes).performClick()

            // Imitate double click on `Create keystroke` button
            onNodeWithContentDescription(createKeystroke)
                .performSimultaneousDoubleClick(composeTestRule.mainClock)
        }
        // Press back
        composeTestRule.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
        // Assert that app is back on `Keystrokes` screen
        composeTestRule.onNodeWithText(keystrokesTitle).assertIsDisplayed()
    }
}

private fun SemanticsNodeInteraction.performSimultaneousDoubleClick(clock: MainTestClock) {
    performClick()
    clock.autoAdvance = false
    performClick()
    clock.autoAdvance = true
}

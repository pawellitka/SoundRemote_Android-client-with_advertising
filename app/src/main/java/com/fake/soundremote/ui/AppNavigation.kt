package com.fake.soundremote.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.fake.soundremote.ui.about.aboutScreen
import com.fake.soundremote.ui.about.navigateToAbout
import com.fake.soundremote.ui.events.eventsScreen
import com.fake.soundremote.ui.events.navigateToEvents
import com.fake.soundremote.ui.home.homeRoute
import com.fake.soundremote.ui.home.homeScreen
import com.fake.soundremote.ui.keystroke.keystrokeCreateScreen
import com.fake.soundremote.ui.keystroke.keystrokeEditScreen
import com.fake.soundremote.ui.keystroke.navigateToKeystrokeCreate
import com.fake.soundremote.ui.keystroke.navigateToKeystrokeEdit
import com.fake.soundremote.ui.keystrokelist.keystrokeListScreen
import com.fake.soundremote.ui.keystrokelist.navigateToKeystrokeList
import com.fake.soundremote.ui.settings.navigateToSettings
import com.fake.soundremote.ui.settings.settingsScreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppNavigation(
    windowSizeClass: WindowSizeClass,
    showSnackbar: (String, SnackbarDuration) -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
    padding: PaddingValues
) {
    val navController = rememberNavController()
    val compactHeight = windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact
    NavHost(
        navController = navController,
        startDestination = homeRoute,
        Modifier
            .padding(padding)
            .consumeWindowInsets(padding)
    ) {
        homeScreen(
            onNavigateToKeystrokeList = navController::navigateToKeystrokeList,
            onNavigateToEvents = navController::navigateToEvents,
            onNavigateToSettings = navController::navigateToSettings,
            onNavigateToAbout = navController::navigateToAbout,
            onEditKeystroke = { navController.navigateToKeystrokeEdit(it) },
            showSnackbar = showSnackbar,
            setFab = setFab,
            compactHeight = compactHeight,
        )
        keystrokeListScreen(
            onCreate = navController::navigateToKeystrokeCreate,
            onEdit = { navController.navigateToKeystrokeEdit(it) },
            onNavigateUp = navController::navigateUp,
            setFab = setFab,
        )
        keystrokeCreateScreen(
            onNavigateUp = navController::navigateUp,
            showSnackbar = showSnackbar,
            setFab = setFab,
            compactHeight = compactHeight,
        )
        keystrokeEditScreen(
            onNavigateUp = navController::navigateUp,
            showSnackbar = showSnackbar,
            setFab = setFab,
            compactHeight = compactHeight,
        )
        eventsScreen(
            onNavigateUp = navController::navigateUp,
            setFab = setFab,
        )
        settingsScreen(
            onNavigateUp = navController::navigateUp,
            setFab = setFab,
        )
        aboutScreen(
            onNavigateUp = navController::navigateUp,
            setFab = setFab,
        )
    }
}

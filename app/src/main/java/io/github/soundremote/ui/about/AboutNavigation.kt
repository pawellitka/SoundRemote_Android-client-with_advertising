package io.github.soundremote.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val ABOUT_ROUTE = "about"

fun NavController.navigateToAbout() {
    navigate(ABOUT_ROUTE)
}

fun NavGraphBuilder.aboutScreen(
    onNavigateUp: () -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
) {
    composable(ABOUT_ROUTE) {
        AboutScreen(
            onNavigateUp = onNavigateUp,
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}

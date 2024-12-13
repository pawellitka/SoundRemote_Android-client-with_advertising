package io.github.soundremote.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object AboutRoute

fun NavController.navigateToAbout() {
    navigate(AboutRoute)
}

fun NavGraphBuilder.aboutScreen(
    onNavigateUp: () -> Unit,
    setFab: ((@Composable () -> Unit)?) -> Unit,
) {
    composable<AboutRoute> {
        AboutScreen(
            onNavigateUp = onNavigateUp,
        )
        LaunchedEffect(Unit) {
            setFab(null)
        }
    }
}

package com.fake.soundremote.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.fake.soundremote.R

@Composable
fun NavigateUpButton(
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.ArrowBack, stringResource(R.string.navigate_up))
    }
}

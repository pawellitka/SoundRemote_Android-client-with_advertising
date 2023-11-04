package com.fake.soundremote.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.fake.soundremote.ui.util.ListItemHeadline
import com.fake.soundremote.ui.util.ListItemSupport

@Composable
internal fun PreferenceItem(
    title: String,
    summary: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clickable(onClick = onClick)
            .then(preferenceItemPadding)
    ) {
        ListItemHeadline(title)
        ListItemSupport(summary)
    }
}

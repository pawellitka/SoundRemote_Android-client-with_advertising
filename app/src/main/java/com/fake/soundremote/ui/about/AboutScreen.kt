package com.fake.soundremote.ui.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fake.soundremote.BuildConfig
import com.fake.soundremote.R
import com.fake.soundremote.ui.components.ListItemHeadline
import com.fake.soundremote.ui.components.NavigateUpButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AboutScreen(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLicense by rememberSaveable { mutableStateOf(false) }
    var licenseText by rememberSaveable { mutableStateOf("") }
    var licenseFile by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val appName = stringResource(R.string.app_name)
    val opusFile = "opus_license.txt"
    val apache2File = "apache_2.txt"

    LaunchedEffect(licenseFile) {
        if (licenseFile.isBlank()) return@LaunchedEffect
        licenseText = getLicense(context, licenseFile)
    }

    Column(modifier) {
        TopAppBar(
            title = {
                Text(stringResource(R.string.about_title_template).format(appName))
            },
            navigationIcon = { NavigateUpButton(onNavigateUp) },
        )
        Text(
            text = appName + ' ' + BuildConfig.VERSION_NAME,
            style = MaterialTheme.typography.titleMedium,
            modifier = paddingMod
        )
        Text(
            text = "© 2024 Aleksandr Shipovskii",
            style = MaterialTheme.typography.bodyLarge,
            modifier = paddingMod
        )
        val loadLicense: (String) -> Unit = { fileName ->
            if (fileName != licenseFile) {
                licenseText = ""
                licenseFile = fileName
            }
            showLicense = true
        }
        Credit(
            name = "Accompanist",
            url = "https://google.github.io/accompanist",
            onShowLicense = { loadLicense(apache2File) },
            context = context,
        )
        Credit(
            name = "Guava",
            url = "https://guava.dev",
            onShowLicense = { loadLicense(apache2File) },
            context = context,
        )
        Credit(
            name = "Hilt",
            url = "https://dagger.dev/hilt/",
            onShowLicense = { loadLicense(apache2File) },
            context = context,
        )
        Credit(
            name = "Opus",
            url = "https://opus-codec.org",
            onShowLicense = { loadLicense(opusFile) },
            context = context,
        )
        Credit(
            name = "Seismic",
            url = "https://github.com/square/seismic",
            onShowLicense = { loadLicense(apache2File) },
            context = context,
        )
        Credit(
            name = "Timber",
            url = "https://github.com/JakeWharton/timber",
            onShowLicense = { loadLicense(apache2File) },
            context = context,
        )
    }
    if (showLicense) {
        AlertDialog(
            onDismissRequest = { showLicense = false },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showLicense = false }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            text = {
                Text(
                    text = licenseText,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                )
            }
        )
    }
}

private suspend fun getLicense(context: Context, fileName: String): String =
    withContext(Dispatchers.IO) {
        try {
            return@withContext context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw IllegalArgumentException("Failed to open asset file: $fileName", e)
        }
    }

private val paddingMod = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 24.dp)

@Composable
private fun Credit(
    name: String,
    url: String,
    onShowLicense: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier,
) {
    Row(modifier.then(paddingMod), verticalAlignment = Alignment.CenterVertically) {
        ListItemHeadline(name, Modifier.weight(1f))
        TextButton(
            onClick = onShowLicense,
        ) {
            Text(stringResource(R.string.show_license))
        }
        TextButton(
            onClick = {
                val webpage: Uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            },
        ) {
            Text(stringResource(R.string.open_homepage))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        onNavigateUp = {},
    )
}

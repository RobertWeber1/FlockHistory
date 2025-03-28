package de.flock_history.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.flock_history.R
import de.flock_history.ui.theme.FlockHistoryTheme

@Composable
fun HomeScreenWithViewModel(
    goToScannerScreen: () -> Unit,
    goToEntryProcessScreen: () -> Unit,
    goToSheepListScreen: () -> Unit,
) {
    HomeScreen(goToScannerScreen, goToEntryProcessScreen, goToSheepListScreen)
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FlockHistoryTheme {
        HomeScreen({}, {}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    goToScannerScreen: () -> Unit,
    goToEntryProcessScreen: () -> Unit,
    goToSheepListScreen: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Text(text = "Home")
            Button(onClick = goToScannerScreen) { Text(stringResource(R.string.home_scanner_btn)) }
            Button(onClick = goToEntryProcessScreen) { Text(stringResource(R.string.home_entry_process_btn)) }
            Button(onClick = goToSheepListScreen) { Text(stringResource(R.string.home_sheep_list_btn)) }
        }
    }
}

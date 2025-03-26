package de.flock_history.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import de.flock_history.MainViewModel
import de.flock_history.ui.theme.FlockHistoryTheme

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    FlockHistoryTheme {
        HomeScreen({})
    }
}

@Composable
fun HomeScreen(goToScannerScreen: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Text(text = "Home")
            Button(onClick = goToScannerScreen) { Text("Scanner") }
        }
    }
}

@Composable
fun HomeScreenWithViewModel(goToScannerScreen: () -> Unit) {
    val viewModel = viewModel<MainViewModel>()
    HomeScreen(goToScannerScreen)
}

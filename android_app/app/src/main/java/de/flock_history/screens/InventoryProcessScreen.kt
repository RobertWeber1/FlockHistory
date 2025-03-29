package de.flock_history.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.flock_history.R
import de.flock_history.ui.theme.FlockHistoryTheme

@Composable
fun InventoryProcessScreenWithViewModel(goBack: () -> Unit) {
    // TODO hook this up to viewModel
    InventoryProcessScreen(goBack, {})
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InventoryProcessScreenPreview() {
    FlockHistoryTheme {
        InventoryProcessScreen({}, {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryProcessScreen(goBack: () -> Unit, inventorizeSheep: (tagID: String) -> Unit) {
    val tagIdState = rememberTextFieldState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.inventory_process_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back_btn),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 5.dp, 5.dp, 0.dp),
                state = tagIdState,
                label = { Text(stringResource(R.string.inventory_process_tag_id)) },
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(5.dp, 5.dp, 5.dp, 0.dp),
                shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp),
                onClick = {
                    inventorizeSheep(tagIdState.text.toString())
                    tagIdState.clearText()
                },
            ) {
                Text(
                    stringResource(
                        R.string.inventory_process_submit
                    ),
                    fontSize = 20.sp,
                )
            }
        }
    }
}

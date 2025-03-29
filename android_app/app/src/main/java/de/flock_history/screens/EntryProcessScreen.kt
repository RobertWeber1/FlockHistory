package de.flock_history.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import de.flock_history.R
import de.flock_history.ui.theme.FlockHistoryTheme

enum class Gender {
    FEMALE,
    MALE,
}

@Composable
fun EntryProcessScreenWithViewModel(goBack: () -> Unit) {
    EntryProcessScreen(goBack, { s: String, i: Int, s1: String, gender: Gender -> })
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EntryProcessScreenPreview() {
    FlockHistoryTheme {
        EntryProcessScreen({}, { s: String, i: Int, s1: String, gender: Gender -> })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryProcessScreen(
    goBack: () -> Unit,
    doEntry: (tagID: String, ageInMonths: Int, genoType: String, gender: Gender) -> Unit
) {
    val tagIdState = rememberTextFieldState()
    var ageInMonthsState by remember { mutableStateOf("12") }
    val genoTypeState = rememberTextFieldState()

    val finishEntryProcess = { gender: Gender ->
        try {
            val ageInMonths = ageInMonthsState.toInt()
            doEntry(
                tagIdState.text.toString(),
                ageInMonths,
                genoTypeState.text.toString(),
                gender,
            )
        } catch (_: NumberFormatException) {
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.entry_process_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                navigationIcon = {
                    IconButton(onClick = goBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            TextField(
                state = tagIdState,
                label = { Text(stringResource(R.string.entry_process_tag_id)) },
            )
            Row {
                TextField(
                    value = ageInMonthsState,
                    onValueChange = {
                        try {
                            val num = it.toInt()
                            if (num < 0) {
                                return@TextField
                            }
                            ageInMonthsState = it
                        } catch (_: NumberFormatException) {
                        }
                    },
                    label = { Text(stringResource(R.string.entry_process_age_in_months)) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                )
                Button(onClick = {}) { Text("-") }
                Button(onClick = {}) { Text("+") }
            }
            TextField(
                state = genoTypeState,
                label = { Text(stringResource(R.string.entry_process_geno_type)) },
            )
            Row {
                Button(onClick = { finishEntryProcess(Gender.FEMALE) }) { Text("female") }
                Button(onClick = { finishEntryProcess(Gender.MALE) }) { Text("male") }
            }
        }
    }
}

package de.flock_history.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import de.flock_history.BleDevice
import de.flock_history.BluetoothDeviceType
import de.flock_history.MainViewModel
import de.flock_history.ui.theme.FlockHistoryTheme

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScannerScreenPreview() {
    val devices = mapOf(Pair("ABCDEF", BleDevice("ABCDEF", "My LE Device", BluetoothDeviceType.DEVICE_TYPE_LE)))
    FlockHistoryTheme {
        ScannerScreen(devices)
    }
}

@Composable
fun ScannerScreen(bleDevices: Map<String, BleDevice>) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            Text(text = "BLE Devices")
            LazyColumn {
                items(bleDevices.values.toList()) { bleDevice ->
                    Text(text = "[${bleDevice.address} - ${bleDevice.type.short()}] ${bleDevice.name}")
                }
            }
        }
    }
}

@Composable
fun ScannerScreenWithViewModel(goBackToHome: () -> Unit) {
    val viewModel = viewModel<MainViewModel>()
    val bleDevices = viewModel.bleDevices.observeAsState()

    ScannerScreen(bleDevices.value!!)
}

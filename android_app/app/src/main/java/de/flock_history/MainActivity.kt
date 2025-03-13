package de.flock_history

import android.Manifest
import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.flock_history.ui.theme.FlockHistoryTheme
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlockHistoryTheme {
        Greeting("Android")
    }
}

@Composable
fun FlockHistory(bleDevices: Map<String, BleDevice>) {
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
fun FlockHistoryThemed(viewModel: MainViewModel) {
    val bleDevices = viewModel.bleDevices.observeAsState()
    FlockHistoryTheme {
        FlockHistory(bleDevices.value!!)
    }
}

class BLEScanner(
    private val onBleDeviceAdded: (Map<String, BleDevice>) -> Unit,
    private val onBleDeviceRemoved: (Map<String, BleDevice>) -> Unit,
) :
    ScanCallback() {
    private val SCAN_PERIOD: Long = 10

    private val executor = Executors.newScheduledThreadPool(1)
    private var context: Context? = null
    private var scanner: BluetoothLeScanner? = null
    private var scanning = false

    @SuppressLint("MissingPermission")
    fun startScan(context: Context, bluetoothLeScanner: BluetoothLeScanner?) {
        this.context = context
        scanner = bluetoothLeScanner

        if (scanner == null) {
            Log.e(FH_TAG, "Scanning failed due to missing scanner")
            return
        }

        Log.i(FH_TAG, "Starting bluetooth LE scan")

        scanner?.startScan(this)
        executor.schedule({
            scanning = false
            scanner?.stopScan(this)
        }, SCAN_PERIOD, TimeUnit.SECONDS)
    }

    override fun onScanFailed(errorCode: Int) {
        Log.e(FH_TAG, "BLE Device scan failed: $errorCode")
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        Log.i(FH_TAG, "Device found (callbackType=$callbackType)")
        if (result == null) {
            return
        }

        processScanResults(listOf(result))
    }

    private fun processScanResults(results: List<ScanResult>) {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(FH_TAG, "Bluetooth connect permission not granted")
            return
        }

        val devices = LinkedHashMap<String, BleDevice>()
        for (result in results) {
            Log.i(FH_TAG, "Device found: ${result.device.address}")

// TODO
//            result.device.connectGatt(context, true, object : BluetoothGattCallback() {
//            })

            devices[result.device.address] = BleDevice(
                result.device.address,
                result.device.name,
                BluetoothDeviceType.fromInt(result.device.type)
            )
        }
        onBleDeviceAdded(devices)
    }
}

class MainActivity : ComponentActivity() {
    val REQUEST_ENABLE_BT = 1

    var bluetoothAdapter: BluetoothAdapter? = null
    val bleScanner =
        BLEScanner(this::onBleDevicesAdded, this::onBleDevicesRemoved)
    val viewModel = MainViewModel()

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i(FH_TAG, "Permission granted")
            } else {
                Log.e(FH_TAG, "Permission not granted")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlockHistoryThemed(viewModel)
        }

        val bluetoothLEAvailable =
            packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        if (!bluetoothLEAvailable) {
            Log.w(FH_TAG, "Device does not support bluetooth low energy")
            return
        }

        Log.i(FH_TAG, "Checking bluetooth permissions")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermission(Manifest.permission.BLUETOOTH_SCAN)
            requestPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            requestPermission(Manifest.permission.BLUETOOTH)
        }

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            Log.w(FH_TAG, "Device does not support bluetooth low energy")
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            Log.w(FH_TAG, "Bluetooth is not enabled, starting intent to enable it")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
            return
        }


        startDeviceScan()
    }

    private fun requestPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i(FH_TAG, "Permission $permission granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, permission) -> {
                // TODO
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                Log.i(FH_TAG, "Permission $permission not granted: should show rationale")
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(permission)
                Log.i(
                    FH_TAG,
                    "Permission $permission not granted: has asked for permission directly"
                )
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        Log.i(FH_TAG, "Bluetooth has been enabled")
        startDeviceScan()
    }

    private fun startDeviceScan() {
        bleScanner.startScan(applicationContext, bluetoothAdapter?.bluetoothLeScanner)
    }

    private fun onBleDevicesAdded(devices: Map<String, BleDevice>) {
        val newDevices = LinkedHashMap<String, BleDevice>()
        newDevices.putAll(viewModel.bleDevices.value!!)
        newDevices.putAll(devices)
        viewModel.bleDevices.postValue(newDevices)
    }

    private fun onBleDevicesRemoved(devices: Map<String, BleDevice>) {
    }
}

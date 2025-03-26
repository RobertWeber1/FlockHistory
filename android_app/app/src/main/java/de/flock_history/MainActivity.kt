package de.flock_history

import android.Manifest
import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.flock_history.screens.HomeScreenWithViewModel
import de.flock_history.screens.ScannerScreenWithViewModel
import de.flock_history.ui.theme.FlockHistoryTheme
import kotlinx.serialization.Serializable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Serializable
object Home

@Serializable
object Scanner

@Composable
fun FlockHistoryApp() {
    val navController = rememberNavController()
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                HomeScreenWithViewModel({ navController.navigate(route = Scanner) })
            }
        }
        composable<Scanner> {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner
            ) {
                ScannerScreenWithViewModel({ navController.navigate(route = Home) })
            }
        }
    }
}

class BLEScanner(
    private val onBleDeviceAdded: (Map<String, BleDevice>) -> Unit,
    private val onBleDeviceRemoved: (Map<String, BleDevice>) -> Unit,
) :
    ScanCallback() {
    private val SCAN_PERIOD_SECONDS: Long = 10

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

        Log.i(FH_TAG, "Starting Bluetooth LE scan")

        scanner?.startScan(this)
        executor.schedule({
            scanning = false
            scanner?.stopScan(this)
            Log.i(FH_TAG, "Stopped Bluetooth LE scan")
        }, SCAN_PERIOD_SECONDS, TimeUnit.SECONDS)
    }

    override fun onScanFailed(errorCode: Int) {
        Log.e(FH_TAG, "BLE Device scan failed: $errorCode")
    }

    private fun checkBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(FH_TAG, "Bluetooth connect permission not granted")
                return true
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(FH_TAG, "Bluetooth permission not granted")
                return true
            }
        }
        return false
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        Log.i(FH_TAG, "Device found (callbackType=$callbackType)")
        if (result == null) {
            return
        }

        if (checkBluetoothPermission()) {
            return
        }

        val devices = LinkedHashMap<String, BleDevice>()

        Log.i(FH_TAG, "Device found: ${result.device.address}")

        result.device.connectGatt(context, true, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                Log.i(FH_TAG, "Connection State Changed")
                if (gatt == null) {
                    return
                }

                if (checkBluetoothPermission()) {
                    return
                }

                gatt.discoverServices()
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                Log.i(FH_TAG, "Services discovered")
                if (gatt == null) {
                    return
                }

                for (service in gatt.services) {
                    Log.i(FH_TAG, "Found service ${service.type}")
                }
            }

            override fun onServiceChanged(gatt: BluetoothGatt) {
                Log.i(FH_TAG, "Service Changed")
            }
        })

        devices[result.device.address] = BleDevice(
            result.device.address,
            result.device.name,
            BluetoothDeviceType.fromInt(result.device.type)
        )

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
            FlockHistoryTheme {
                FlockHistoryApp()
            }
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
            Log.i(FH_TAG, "Checking location permission")
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
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

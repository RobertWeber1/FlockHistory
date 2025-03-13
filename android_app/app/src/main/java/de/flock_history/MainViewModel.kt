package de.flock_history

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.MutableLiveData

enum class BluetoothDeviceType(val value: Int) {
    DEVICE_TYPE_UNKNOWN(0),
    DEVICE_TYPE_CLASSIC(1),
    DEVICE_TYPE_LE(2),
    DEVICE_TYPE_DUAL(3);

    fun short(): String {
        return when (this) {
            DEVICE_TYPE_UNKNOWN -> "UNKNOWN"
            DEVICE_TYPE_CLASSIC -> "CLASSIC"
            DEVICE_TYPE_LE -> "LE"
            DEVICE_TYPE_DUAL -> "DUAL"
        }
    }

    companion object {
        fun fromInt(value: Int): BluetoothDeviceType {
            return when (value) {
                BluetoothDevice.DEVICE_TYPE_UNKNOWN -> DEVICE_TYPE_UNKNOWN
                BluetoothDevice.DEVICE_TYPE_CLASSIC -> DEVICE_TYPE_CLASSIC
                BluetoothDevice.DEVICE_TYPE_LE -> DEVICE_TYPE_LE
                BluetoothDevice.DEVICE_TYPE_DUAL -> DEVICE_TYPE_DUAL
                else -> DEVICE_TYPE_UNKNOWN
            }
        }
    }
}

data class BleDevice(
    val address: String,
    val name: String,
    val type: BluetoothDeviceType,
)

class MainViewModel {
    val bleDevices = MutableLiveData<Map<String, BleDevice>>(LinkedHashMap())
}

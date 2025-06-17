package com.example.cancan

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class BleManager(
    private val context: Context,
    private val device: BluetoothDevice
) {

    companion object {
        val SERVICE_UUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private var characteristic: BluetoothGattCharacteristic? = null

    private var onNotifyCallback: ((String) -> Unit)? = null
    private var onConnectedCallback: (() -> Unit)? = null
    private var onFailedCallback: (() -> Unit)? = null

    fun connectAndDiscoverServices(
        onNotify: (String) -> Unit,
        onConnected: () -> Unit,
        onFailed: () -> Unit
    ) {
        this.onNotifyCallback = onNotify
        this.onConnectedCallback = onConnected
        this.onFailedCallback = onFailed

        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun writeToCharacteristic(data: String) {
        val value = data.toByteArray(Charsets.UTF_8)
        characteristic?.let {
            it.value = value
            bluetoothGatt?.writeCharacteristic(it)
            Log.d("BleManager", "📤 전송됨: $data")
        }
    }

    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        Log.d("BleManager", "🔌 연결 해제됨")
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BleManager", "✅ BLE 연결 성공")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.w("BleManager", "❌ BLE 연결 해제됨")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(SERVICE_UUID)
            characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)

            if (characteristic != null) {
                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic!!.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)

                onConnectedCallback?.invoke()
            } else {
                Log.e("BleManager", "⚠️ 특성 발견 실패")
                onFailedCallback?.invoke()
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val data = characteristic.value.toString(Charsets.UTF_8)
            Log.d("BleManager", "📥 수신: $data")
            onNotifyCallback?.invoke(data)
        }
    }
}

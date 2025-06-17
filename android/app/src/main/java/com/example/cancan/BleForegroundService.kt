package com.example.cancan.ble

import android.app.*
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class BleForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "BLE_CHANNEL"
        const val NOTIFICATION_ID = 1
        val SERVICE_UUID: UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    }

    private val binder = LocalBinder()
    private var bluetoothGatt: BluetoothGatt? = null
    private var characteristic: BluetoothGattCharacteristic? = null

    private var notifyListener: ((String) -> Unit)? = null
    private var onConnectedListener: (() -> Unit)? = null
    private var onDisconnectedListener: (() -> Unit)? = null

    private val messageQueue: Queue<String> = ConcurrentLinkedQueue()
    private var isProcessingQueue = false
    var isBleReady: Boolean = false
        private set

    private var onBleReadyListener: (() -> Unit)? = null
    fun setOnBleReadyListener(listener: () -> Unit) {
        onBleReadyListener = listener
        if (isBleReady) {
            runOnUiThread { onBleReadyListener?.invoke() }
        }
    }

    private var modeAckCallback: (() -> Unit)? = null
    private var pendingMode: String? = null

    inner class LocalBinder : Binder() {
        fun getService(): BleForegroundService = this@BleForegroundService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("점자 BLE 연결 중")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        Log.d("BleForegroundService", "✅ 서비스 생성")
    }

    fun connect(device: BluetoothDevice, context: Context, onConnected: (() -> Unit)? = null) {
        Log.d("BleForegroundService", "connectGatt 호출: ${device.address}")
        onConnectedListener = onConnected
        bluetoothGatt?.disconnect()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
        isBleReady = false
        characteristic = null
        messageQueue.clear()
        isProcessingQueue = false
    }

    fun send(data: String) {
        messageQueue.add(data)
        processQueue()
    }

    private fun processQueue() {
        if (isProcessingQueue) return
        if (!isBleReady || characteristic == null || bluetoothGatt == null) return
        val msg = messageQueue.peek() ?: return
        isProcessingQueue = true
        characteristic!!.value = msg.toByteArray()
        val success = bluetoothGatt?.writeCharacteristic(characteristic) ?: false
        if (!success) {
            Log.e("BleForegroundService", "⚠️ writeCharacteristic 실패, 재시도 예정")
            isProcessingQueue = false
            Handler(Looper.getMainLooper()).postDelayed({ processQueue() }, 100)
        } else {
            Log.d("BleForegroundService", "📤 BLE 전송: $msg")
        }
    }

    fun switchMode(targetMode: String, onAck: () -> Unit) {
        pendingMode = targetMode
        modeAckCallback = onAck
        send("mode:$targetMode\n")
    }

    fun setNotifyListener(callback: (String) -> Unit) {
        Log.d("BleForegroundService", "👂 알림 리스너 등록")
        notifyListener = callback
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d("BleForegroundService", "onConnectionStateChange: status=$status, newState=$newState")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d("BleForegroundService", "✅ BLE 연결 성공")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.w("BleForegroundService", "❌ BLE 연결 해제")
                    runOnUiThread { onDisconnectedListener?.invoke() }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d("BleForegroundService", "🔍 서비스 탐색 완료: status=$status")
            val service = gatt.getService(SERVICE_UUID)
            characteristic = service?.getCharacteristic(CHARACTERISTIC_UUID)
            if (characteristic != null) {
                Log.d("BleForegroundService", "🔄 알림 활성화")
                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic!!.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                val result = gatt.writeDescriptor(descriptor)
                Log.d("BleForegroundService", "writeDescriptor 결과: $result")
                if (!result) {
                    Log.e("BleForegroundService", "⚠️ 알림 활성화 writeDescriptor 실패")
                }
            } else {
                Log.e("BleForegroundService", "⚠️ 서비스/특성 발견 실패")
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            Log.d("BleForegroundService", "onDescriptorWrite: status=$status")
            if (descriptor.characteristic.uuid == CHARACTERISTIC_UUID) {
                Log.d("BleForegroundService", "🔔 알림 활성화 완료, BLE 명령 전송 가능")
                isBleReady = true
                runOnUiThread { onBleReadyListener?.invoke() }
                runOnUiThread { onConnectedListener?.invoke() }
                processQueue()
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val message = characteristic.value.toString(Charsets.UTF_8)
            Log.d("BleForegroundService", "📥 수신: $message")
            if (pendingMode != null && message.trim() == "mode:${pendingMode}:ok") {
                modeAckCallback?.invoke()
                modeAckCallback = null
                pendingMode = null
                return
            }
            runOnUiThread { notifyListener?.invoke(message) }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.d("BleForegroundService", "onCharacteristicWrite: status=$status")
            messageQueue.poll()
            isProcessingQueue = false
            processQueue()
        }
    }

    private fun runOnUiThread(action: () -> Unit) {
        Handler(Looper.getMainLooper()).post(action)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "BLE Foreground Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onDestroy() {
        Log.d("BleForegroundService", "🔌 서비스 종료")
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        super.onDestroy()
    }
}

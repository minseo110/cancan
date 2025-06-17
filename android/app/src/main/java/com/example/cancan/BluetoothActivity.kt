package com.example.cancan

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cancan.ble.BleForegroundService

class BluetoothActivity : AppCompatActivity() {

    companion object {
        var connectedDevice: BluetoothDevice? = null
    }

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var deviceListView: ListView
    private lateinit var refreshButton: ImageView

    private val deviceList = mutableListOf<String>()
    private val deviceMap = mutableMapOf<String, BluetoothDevice>()
    private val REQUEST_CODE_BLUETOOTH = 101

    // BLE 서비스 바인딩 관련
    private var bleService: BleForegroundService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as BleForegroundService.LocalBinder
            bleService = localBinder.getService()
            isBound = true
            Log.d("BluetoothActivity", "서비스 바인딩 완료")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bleService = null
            isBound = false
            Log.d("BluetoothActivity", "서비스 연결 해제")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        loadingSpinner = findViewById(R.id.loadingSpinner)
        deviceListView = findViewById(R.id.deviceListView)
        refreshButton = findViewById(R.id.btnRefresh)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.btnHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        deviceListView.setOnItemClickListener { _, _, position, _ ->
            val deviceName = deviceList[position]
            val device = deviceMap[deviceName]
            if (device != null) {
                connectedDevice = device
                Log.d("BluetoothActivity", "기기 선택: $deviceName (${device.address})")
                startForegroundServiceWithDevice(device)
                // 서비스 바인딩이 완료될 때까지 대기 후 connect 호출
                waitForServiceAndConnect(device)
            } else {
                Log.e("BluetoothActivity", "기기 선택 오류: $deviceName")
                showConnectionResult(false)
            }
        }

        refreshButton.setOnClickListener {
            startScan()
        }

        // BLE 서비스 바인딩
        Intent(this, BleForegroundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startForegroundService(intent)
        }

        checkPermissionsAndStart()
    }

    private fun waitForServiceAndConnect(device: BluetoothDevice) {
        // 서비스 바인딩이 완료될 때까지 100ms마다 체크
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                if (isBound && bleService != null) {
                    Log.d("BluetoothActivity", "서비스 바인딩 확인, connect 호출")
                    bleService?.connect(device, this@BluetoothActivity)
                    // BLE 준비 완료 시점에만 다이얼로그를 띄움
                    waitForBleReadyAndShowDialog()
                } else {
                    Log.d("BluetoothActivity", "서비스 바인딩 대기 중...")
                    Handler(Looper.getMainLooper()).postDelayed(this, 100)
                }
            }
        }, 100)
    }

    private fun checkPermissionsAndStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), REQUEST_CODE_BLUETOOTH
            )
        } else {
            startScan()
        }
    }

    private fun startScan() {
        deviceList.clear()
        deviceMap.clear()
        updateList()

        loadingSpinner.visibility = ProgressBar.VISIBLE
        deviceListView.visibility = ListView.INVISIBLE

        val scanner = bluetoothAdapter.bluetoothLeScanner
        scanner?.startScan(scanCallback)

        Handler(Looper.getMainLooper()).postDelayed({
            scanner?.stopScan(scanCallback)
            loadingSpinner.visibility = ProgressBar.GONE
            deviceListView.visibility = ListView.VISIBLE
        }, 5000)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val name = device.name ?: device.address
            if (!deviceList.contains(name)) {
                deviceList.add(name)
                deviceMap[name] = device
                updateList()
            }
        }
    }

    private fun updateList() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        deviceListView.adapter = adapter
    }

    private fun startForegroundServiceWithDevice(device: BluetoothDevice) {
        val intent = Intent(this, BleForegroundService::class.java).apply {
            putExtra("device_address", device.address)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        Log.d("BluetoothActivity", "포그라운드 서비스 시작: ${device.address}")
    }

    private fun waitForBleReadyAndShowDialog() {
        if (!isBound || bleService == null) {
            Toast.makeText(this, "BLE 서비스 연결 중입니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val progressDialog = AlertDialog.Builder(this)
            .setTitle("연결 중")
            .setMessage("기기와 연결 및 준비 중입니다. 잠시만 기다려 주세요.")
            .setCancelable(false)
            .create()
        progressDialog.show()

        bleService?.setOnBleReadyListener {
            progressDialog.dismiss()
            showConnectionResult(true)
        }
    }

    private fun showConnectionResult(success: Boolean) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(if (success) "연결 성공" else "연결 실패")
            .setMessage(
                if (success)
                    "기기와 연결 및 준비가 완료되었습니다!\n이제 시작할 수 있습니다."
                else
                    "기기 연결에 실패했습니다.\n다시 시도하시겠습니까?"
            )
            .setPositiveButton(if (success) "시작하기" else "재검색") { _, _ ->
                if (success) {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    startScan()
                }
            }
            .setNegativeButton("닫기", null)
            .create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_BLUETOOTH && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startScan()
        } else {
            Toast.makeText(this, "블루투스 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        if (isBound) unbindService(connection)
        super.onDestroy()
    }
}

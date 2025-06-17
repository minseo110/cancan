package com.example.cancan

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.cancan.ble.BleForegroundService
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private var bleService: BleForegroundService? = null
    private var isBound = false

    private val REQUEST_IMAGE_CAPTURE = 1001
    private val REQUEST_CAMERA_PERMISSION = 1002
    private lateinit var photoUri: Uri
    private lateinit var photoFile: File

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
            val binder = serviceBinder as BleForegroundService.LocalBinder
            bleService = binder.getService()
            isBound = true
            Log.d("HomeActivity", "BLE 서비스 바인딩 완료")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bleService = null
            isBound = false
            Log.d("HomeActivity", "BLE 서비스 연결 해제")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        val btnDictionary = findViewById<Button>(R.id.btnDictionary)
        val btnVoice = findViewById<Button>(R.id.btnVoice)
        val btnQuiz = findViewById<Button>(R.id.btnQuiz)

        Intent(this, BleForegroundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            startForegroundService(intent)
        }

        btnBack.setOnClickListener { finish() }
        btnHome.setOnClickListener { }

        btnDictionary.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                openCameraDirectly()
            }
        }

        btnVoice.setOnClickListener {
            if (isBound && bleService?.isBleReady == true) {
                bleService?.send("mode:output\n")
                startActivity(Intent(this, VoiceActivity::class.java))
                overridePendingTransition(0, 0)
            } else {
                Toast.makeText(this, "BLE 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnQuiz.setOnClickListener {
            if (isBound && bleService?.isBleReady == true) {
                bleService?.send("mode:input\n")
                startActivity(Intent(this, QuizStartActivity::class.java))
                overridePendingTransition(0, 0)
            } else {
                Toast.makeText(this, "BLE 준비 중입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCameraDirectly() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("BRAILLE_${timestamp}", ".jpg", storageDir)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCameraDirectly()
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("image_uri", photoUri.toString())
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
}

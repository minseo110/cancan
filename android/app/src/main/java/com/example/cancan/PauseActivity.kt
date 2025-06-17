package com.example.cancan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PauseActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent
    private lateinit var pauseButton: ImageView

    private val REQUEST_RECORD_AUDIO_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pause)

        pauseButton = findViewById(R.id.pauseIcon)
        pauseButton.visibility = View.GONE

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)

        btnBack.setOnClickListener {
            stopAndDestroyRecognizer()
            finish()
            overridePendingTransition(0, 0)
        }

        btnHome.setOnClickListener {
            stopAndDestroyRecognizer()
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            startSpeechRecognition()
        }

        pauseButton.setOnClickListener {
            speechRecognizer.stopListening()
        }
    }

    private fun startSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                runOnUiThread {
                    pauseButton.visibility = View.VISIBLE
                }
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val spokenText = matches?.get(0) ?: "결과 없음"
                stopAndDestroyRecognizer()

                // ✅ BLE 기기 연결 확인
                if (BluetoothActivity.connectedDevice == null) {
                    Toast.makeText(this@PauseActivity, "BLE 기기가 연결되지 않았습니다.", Toast.LENGTH_LONG).show()
                    return
                }

                val intent = Intent(this@PauseActivity, VtoBActivity::class.java)
                intent.putExtra("recognized_text", spokenText)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_CLIENT -> "음성 인식을 시작할 수 없습니다. 다시 시도해주세요."
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류입니다."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "마이크 권한이 필요합니다."
                    else -> "에러 발생: $error"
                }

                stopAndDestroyRecognizer()
                val intent = Intent(this@PauseActivity, VtoBActivity::class.java)
                intent.putExtra("recognized_text", message)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(speechIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            val intent = Intent(this, VtoBActivity::class.java)
            intent.putExtra("recognized_text", "마이크 권한이 없어 음성 인식을 시작할 수 없습니다.")
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndDestroyRecognizer()
    }

    private fun stopAndDestroyRecognizer() {
        if (::speechRecognizer.isInitialized) {
            try {
                speechRecognizer.stopListening()
            } catch (e: Exception) {
                Log.w("STT", "stopListening 실패: ${e.message}")
            }
            try {
                speechRecognizer.destroy()
            } catch (e: Exception) {
                Log.w("STT", "destroy 실패: ${e.message}")
            }
        }
    }
}

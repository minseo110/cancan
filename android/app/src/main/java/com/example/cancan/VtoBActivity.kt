package com.example.cancan

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cancan.ble.BleForegroundService
import java.util.*

class VtoBActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var tts: TextToSpeech

    private lateinit var bleService: BleForegroundService
    private var isBound = false
    private var ttsReady = false
    private var isReady = false
    private var isActivityVisible = false

    private lateinit var brailleChunks: List<JumjaroConverter.BrailleChunk>
    private var currentChunkIndex = 0
    private var currentDotIndex = 0
    private var resultText: String = ""

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            Log.d("VtoB", "🔗 서비스 연결됨")
            bleService = (binder as BleForegroundService.LocalBinder).getService()
            isBound = true
            bleService.setNotifyListener { handleBleMessage(it) }

            if (bleService.isBleReady) {
                Log.d("VtoB", "🟢 BLE 준비 완료, mode:output 전송")
                bleService.send("mode:output\n")
            } else {
                Toast.makeText(this@VtoBActivity, "BLE 준비 중입니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(0, 0)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("VtoB", "🔗 서비스 연결 해제")
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vtob)

        resultTextView = findViewById(R.id.resultTextView)
        backButton     = findViewById(R.id.btnBack)
        homeButton     = findViewById(R.id.btnHome)

        // 1) 뒤로/홈 버튼 리스너는 항상 등록해 둡니다
        backButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        homeButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
            overridePendingTransition(0, 0)
        }

        // 2) 음성 인식 결과 확인
        resultText = intent.getStringExtra("recognized_text") ?: ""
        if (resultText.isBlank() || resultText == "에러 발생: 7") {
            // 인식 실패 TTS 안내 및 재시도
            initializeTtsOnce("retry") {
                val failMsg = "음성 인식에 실패하였습니다. 다시 시도해주세요."
                speak(failMsg, "retry")
            }
            return
        }

        // 3) 정상 흐름: 점자 청크 변환
        brailleChunks = JumjaroConverter().toBrailleChunks(resultText)
        Log.d("VtoB", "brailleChunks: $brailleChunks")

        // 4) 화면에 글자 + 6점 벡터 표시
        resultTextView.text = buildString {
            append("원문: $resultText\n\n")
            brailleChunks.forEach { chunk ->
                append("[${chunk.sourceText}]\n")
                chunk.dotArrays.forEach { dots ->
                    append("  [${dots.joinToString(",")} ]\n")
                }
            }
        }

        // 5) BLE 서비스 바인딩
        Intent(this, BleForegroundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        // 6) TTS 초기화 (일반 흐름용)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language  = Locale.KOREAN
                ttsReady      = true
                setTtsListener()
                tryStartBrailleOutput()
            }
        }
    }

    /**
     * 음성 인식 실패 전용 TTS 초기화 + 한 번만 쓰는 UtteranceProgressListener
     */
    private fun initializeTtsOnce(utteranceId: String, onInit: () -> Unit) {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
                ttsReady     = true
                // 실패 안내 후 VoiceActivity로 돌아가는 리스너
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(id: String?) {}
                    override fun onError(id: String?) {}
                    override fun onDone(id: String?) {
                        if (id == utteranceId) {
                            runOnUiThread {
                                startActivity(Intent(this@VtoBActivity, VoiceActivity::class.java))
                                finish()
                                overridePendingTransition(0, 0)
                            }
                        }
                    }
                })
                onInit()
            } else {
                // TTS 초기화 실패 시 바로 돌려보내기
                startActivity(Intent(this, VoiceActivity::class.java))
                finish()
                overridePendingTransition(0, 0)
            }
        }
    }

    /**
     * 브라유 출력 흐름용 TTS 리스너 (intro, chunk_*, finished)
     */
    private fun setTtsListener() {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onError(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                when (utteranceId) {
                    "intro" -> if (isReady && isActivityVisible) {
                        currentChunkIndex = 0
                        currentDotIndex   = 0
                        // 첫 글자 TTS
                        val first = brailleChunks[0].sourceText
                        speak(first, "chunk_0")
                    }
                    "finished" -> if (isActivityVisible) {
                        runOnUiThread {
                            startActivity(Intent(this@VtoBActivity, VoiceActivity::class.java))
                            finish()
                            overridePendingTransition(0, 0)
                        }
                    }
                    else -> if (utteranceId?.startsWith("chunk_") == true) {
                        val idx = utteranceId.removePrefix("chunk_").toInt()
                        if (idx == currentChunkIndex) sendCurrentDot()
                    }
                }
            }
        })
    }

    /**
     * 브라유 출력 시작 안내
     */
    private fun tryStartBrailleOutput() {
        if (ttsReady && isReady && isActivityVisible) {
            currentChunkIndex = 0
            currentDotIndex   = 0
            speak("$resultText 의 점자를 출력합니다", "intro")
        }
    }

    /**
     * BLE 메시지 핸들링
     */
    private fun handleBleMessage(data: String) {
        when (data.trim()) {
            "mode:output:ok", "ready" -> {
                isReady = true
                tryStartBrailleOutput()
            }
            "reset" -> {
                currentDotIndex = 0
                sendCurrentDot()
            }
            "next" -> {
                currentDotIndex++
                val chunk = brailleChunks[currentChunkIndex]
                if (currentDotIndex < chunk.dotArrays.size) {
                    sendCurrentDot()
                } else {
                    currentChunkIndex++
                    if (currentChunkIndex < brailleChunks.size) {
                        currentDotIndex = 0
                        speak(
                            brailleChunks[currentChunkIndex].sourceText,
                            "chunk_$currentChunkIndex"
                        )
                    } else {
                        bleService.send("done\n")
                        speak("출력이 끝났습니다", "finished")
                    }
                }
            }
        }
    }

    private fun sendCurrentDot() {
        val msg = brailleChunks[currentChunkIndex].dotArrays[currentDotIndex]
            .joinToString(",") + "\n"
        bleService.send(msg)
    }

    private fun speak(text: String, utteranceId: String) {
        if (!ttsReady) return
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        super.onPause()
        isActivityVisible = false
    }

    override fun onDestroy() {
        if (isBound) unbindService(connection)
        tts.shutdown()
        super.onDestroy()
    }
}

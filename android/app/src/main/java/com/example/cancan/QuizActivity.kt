package com.example.cancan

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cancan.ble.BleForegroundService
import com.example.cancan.database.AppDatabase
import com.example.cancan.database.BrailleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class QuizActivity : AppCompatActivity() {

    private lateinit var quizText: TextView
    private lateinit var btnHome: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var tts: TextToSpeech

    private lateinit var bleService: BleForegroundService
    private var isBound = false
    private var ttsReady = false
    private var bleReady = false
    private var quizReady = false

    private var quizList: List<BrailleEntity> = emptyList()
    private var currentIndex = 0
    private var score = 0
    private val userInput = mutableListOf<List<Int>>()

    private var outputDots: List<List<Int>> = emptyList()
    private var outputIndex = 0

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as BleForegroundService.LocalBinder
            bleService = localBinder.getService()
            isBound = true
            bleService.setNotifyListener { onBleMessageReceived(it) }

            if (bleService.isBleReady) {
                bleService.send("mode:input\n")
                bleReady = true
                tryStartQuiz()
            } else {
                Toast.makeText(this@QuizActivity, "BLE 준비 중입니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(0, 0)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            bleReady = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        quizText = findViewById(R.id.quizText)
        btnHome = findViewById(R.id.btnHome)
        btnBack = findViewById(R.id.btnBack)

        Intent(this, BleForegroundService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
                ttsReady = true
                tryStartQuiz()
            } else {
                Toast.makeText(this, "TTS 엔진 초기화 실패", Toast.LENGTH_SHORT).show()
                finish()
                overridePendingTransition(0, 0)
            }
        }

        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
        btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
            overridePendingTransition(0, 0)
        }

        loadQuiz()
    }

    private fun loadQuiz() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(this@QuizActivity)
            val all = db.brailleDao().getAll()
            quizList = all.shuffled().take(5)
            currentIndex = 0
            score = 0
            runOnUiThread {
                if (quizList.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "퀴즈 데이터가 없습니다", Toast.LENGTH_SHORT).show()
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    tryStartQuiz()
                }
            }
        }
    }

    private fun tryStartQuiz() {
        if (ttsReady && bleReady && quizList.isNotEmpty() && !quizReady) {
            quizReady = true
            showNextProblem()
        }
    }

    private fun showNextProblem() {
        if (currentIndex >= quizList.size) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("score", score)
            startActivity(intent)
            finish()
            overridePendingTransition(0, 0)
            return
        }

        userInput.clear()
        outputDots = emptyList()
        outputIndex = 0

        val current = quizList[currentIndex]
        val ttsText = if (current.category == "약자") current.text else getTtsName(current.text)
        val hint = "${current.category} $ttsText"
        quizText.text = "문제 ${currentIndex + 1}: ${current.category} ${current.text}"
        val particle = getEulReul(ttsText)
        speak("${currentIndex + 1}번 문제, $hint$particle 입력하세요")
        bleService.send("mode:input\n")
    }

    private fun onBleMessageReceived(data: String) {
        val lines = data.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        for (msg in lines) {
            when (msg) {
                "reset" -> {
                    speak("다시 출력합니다")
                    outputIndex = 0
                    sendCurrentBraille()
                }
                "next" -> {
                    if (outputDots.isNotEmpty()) {
                        outputIndex++
                        if (outputIndex < outputDots.size) {
                            sendCurrentBraille()
                        } else {
//                            speak("출력이 끝났습니다")
                            bleService.send("done\n")
                            bleService.send("mode:input\n")
                            currentIndex++
                            showNextProblem()
                        }
                    }
                }
                "enter" -> {
                    val expectedDots = quizList[currentIndex].answerBraille

                    val filteredUserInput = userInput.filter { it.any { dot -> dot != 0 } }
                    val userAnswer = filteredUserInput.map { it.joinToString("") }
                    val correctAnswer = expectedDots.map { it.joinToString("") }

                    val isCorrect = (userAnswer.size == correctAnswer.size && userAnswer == correctAnswer)
                    if (isCorrect) score++
                    val feedback = if (isCorrect) "정답입니다. 점자를 출력합니다" else "오답입니다. 정답 점자를 출력합니다"
                    speak(feedback)
                    outputDots = expectedDots
                    outputIndex = 0
                    bleService.send("mode:output\n")
                    Handler(Looper.getMainLooper()).postDelayed({ sendCurrentBraille() }, 1500)
                }
                else -> {
                    val parts = msg.split(",").mapNotNull { it.toIntOrNull() }
                    if (parts.size == 6 && parts.any { it != 0 }) {
                        userInput.add(parts)
                    }
                }
            }
        }
    }

    private fun sendCurrentBraille() {
        if (outputIndex < outputDots.size) {
            val dot = outputDots[outputIndex]
            val msg = dot.joinToString(",") + "\n"
            bleService.send(msg)
        }
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (isBound) unbindService(connection)
        tts.shutdown()
        super.onDestroy()
    }

    fun getEulReul(word: String): String {
        val lastChar = word.lastOrNull() ?: return "를"
        val lastCharCode = lastChar.code
        if (lastChar !in '가'..'힣') return "를"
        val hasBatchim = (lastCharCode - 0xAC00) % 28 != 0
        return if (hasBatchim) "을" else "를"
    }

    fun getTtsName(symbol: String): String {
        val map = mapOf(
            "ㄱ" to "기역", "ㄲ" to "쌍기역", "ㄴ" to "니은", "ㄷ" to "디귿", "ㄸ" to "쌍디귿",
            "ㄹ" to "리을", "ㅁ" to "미음", "ㅂ" to "비읍", "ㅃ" to "쌍비읍", "ㅅ" to "시옷",
            "ㅆ" to "쌍시옷", "ㅇ" to "이응", "ㅈ" to "지읒", "ㅉ" to "쌍지읒", "ㅊ" to "치읓",
            "ㅋ" to "키읔", "ㅌ" to "티읕", "ㅍ" to "피읖", "ㅎ" to "히읏",
            "ㅏ" to "아", "ㅐ" to "아이 애", "ㅑ" to "야", "ㅒ" to "야이 얘", "ㅓ" to "어", "ㅔ" to "어이 에",
            "ㅕ" to "여", "ㅖ" to "여이 예", "ㅗ" to "오", "ㅛ" to "요", "ㅜ" to "우", "ㅠ" to "유",
            "ㅡ" to "으", "ㅣ" to "이", "ㅘ" to "와", "ㅙ" to "오애 왜", "ㅚ" to "오이 외",
            "ㅝ" to "워", "ㅞ" to "우에 웨", "ㅟ" to "위", "ㅢ" to "의", "0" to "영", "1" to "일", "2" to "이",
            "3" to "삼", "4" to "사", "5" to "오", "6" to "육", "7" to "칠", "8" to "팔", "9" to "구"

        )

        return symbol.map { ch -> map[ch.toString()] ?: ch.toString() }.joinToString(" ")
    }



}

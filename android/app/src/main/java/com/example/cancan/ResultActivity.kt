package com.example.cancan

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ResultActivity : AppCompatActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 5)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnHome = findViewById<ImageButton>(R.id.btnHome)

        val resultText = "총 ${score * 20} 점입니다"
        findViewById<TextView>(R.id.resultTextView).text = resultText

        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN
                tts.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        // 🔙 뒤로가기 버튼 → 이전 화면
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // 🏠 홈 버튼 → HomeActivity
        btnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tts.shutdown()
    }
}

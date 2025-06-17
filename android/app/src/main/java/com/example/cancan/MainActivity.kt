package com.example.cancan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cancan.init.insertAllBrailleData
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            insertAllBrailleData(applicationContext)
        }

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnConnect = findViewById<Button>(R.id.btnConnect)

        btnStart.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0, 0)
        }

        btnConnect.setOnClickListener {
            startActivity(Intent(this, BluetoothActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }
}
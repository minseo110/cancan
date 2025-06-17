package com.example.cancan

import android.content.Context
import android.util.Log
import org.json.JSONObject

private var brailleMap: Map<String, String>? = null

fun initializeBrailleMap(context: Context) {
    if (brailleMap != null) return
    try {
        val json = context.assets.open("braille_map.json")
            .bufferedReader().use { it.readText() }
        val obj = JSONObject(json)
        brailleMap = obj.keys().asSequence().associateWith { obj.getString(it) }

        Log.d("UnicodeConvert", "✅ braille_map.json loaded. Size = ${brailleMap!!.size}")
    } catch (e: Exception) {
        Log.e("UnicodeConvert", "❌ Failed to load braille_map.json", e)
        brailleMap = emptyMap()
    }
}

fun convertToBrailleUnicode(vec: String, context: Context): String {
    if (vec.length != 6 || !vec.all { it == '0' || it == '1' }) {
        Log.d("UnicodeConvert", "❌ Invalid vec: $vec")
        return "?"
    }

    if (brailleMap == null) {
        Log.d("UnicodeConvert", "🔁 brailleMap 초기화 필요")
        initializeBrailleMap(context)
    }

    val result = brailleMap?.get(vec) ?: "?"
    Log.d("UnicodeConvert", "🔤 $vec → $result")
    return result
}

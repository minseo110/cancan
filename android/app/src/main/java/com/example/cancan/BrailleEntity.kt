package com.example.cancan.database

import androidx.room.Entity
import androidx.room.TypeConverters

@Entity(tableName = "braille_table", primaryKeys = ["category", "text"])
data class BrailleEntity(
    val category: String,
    val text: String,
    val braille: String,
    val answerBraille: List<List<Int>>
)
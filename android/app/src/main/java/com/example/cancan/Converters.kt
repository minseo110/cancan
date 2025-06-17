package com.example.cancan.database

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromNestedList(list: List<List<Int>>): String =
        list.joinToString("|") { it.joinToString(",") }

    @TypeConverter
    fun toNestedList(data: String): List<List<Int>> =
        if (data.isBlank()) emptyList()
        else data.split("|").map { it.split(",").map { it.toInt() } }
}
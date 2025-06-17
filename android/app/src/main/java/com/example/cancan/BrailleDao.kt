package com.example.cancan.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BrailleDao {
    @Query("SELECT * FROM braille_table WHERE category = :category AND text = :text")
    suspend fun getByCategoryAndText(category: String, text: String): BrailleEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: BrailleEntity)

    @Query("SELECT * FROM braille_table")
    suspend fun getAll(): List<BrailleEntity>

}
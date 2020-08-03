package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "practice_words",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("exercise_id"),
        childColumns = arrayOf("exercise_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PracticeWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: Int,
    @ColumnInfo(name = "time") val time: Date = Date(),
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "result") val result: Boolean
)
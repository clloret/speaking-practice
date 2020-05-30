package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "exercise_attempts",
    foreignKeys = [ForeignKey(
        entity = Exercise::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("exercise_id"),
        onDelete = CASCADE
    )]
)
data class ExerciseAttempt(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "exercise_id") val exerciseId: Int,
    @ColumnInfo(name = "time") val time: Date = Date(),
    @ColumnInfo(name = "result") val result: Boolean,
    @ColumnInfo(name = "recognized_text") val recognizedText: String
)
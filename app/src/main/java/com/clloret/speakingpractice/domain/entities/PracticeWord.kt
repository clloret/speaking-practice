package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "practice_words",
    foreignKeys = [ForeignKey(
        entity = ExerciseAttempt::class,
        parentColumns = arrayOf("exercise_attempt_id"),
        childColumns = arrayOf("exercise_attempt_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PracticeWord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "practice_word_id")
    val id: Int = 0,
    @ColumnInfo(name = "exercise_attempt_id", index = true) val exerciseAttemptId: Int,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "result") val result: Boolean
)
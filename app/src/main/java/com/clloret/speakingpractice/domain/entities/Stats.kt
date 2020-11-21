package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import kotlin.math.roundToInt

data class Stats(
    @ColumnInfo(name = "total_attempts") val totalAttempts: Int,
    @ColumnInfo(name = "correct_attempts") val correctAttempts: Int,
    @ColumnInfo(name = "incorrect_attempts") val incorrectAttempts: Int,
    @ColumnInfo(name = "success_rate") val successRate: Int,
    @ColumnInfo(name = "total_exercises") val totalExercises: Int,
    @ColumnInfo(name = "practiced_exercises") val practicedExercises: Int,
    @ColumnInfo(name = "non_practiced_exercises") val nonPracticedExercises: Int
) {
    val percentagePracticedExercises: Int
        get() = (practicedExercises * 100 / totalExercises.toDouble()).roundToInt()
}

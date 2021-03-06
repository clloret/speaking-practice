package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import kotlin.math.roundToInt

data class CalculatedStats(
    @ColumnInfo(name = "total_attempts") val totalAttempts: Int,
    @ColumnInfo(name = "correct_attempts") val correctAttempts: Int,
    @ColumnInfo(name = "incorrect_attempts") val incorrectAttempts: Int,
    @ColumnInfo(name = "total_exercises") val totalExercises: Int,
    @ColumnInfo(name = "practiced_exercises") val practicedExercises: Int,
    @ColumnInfo(name = "non_practiced_exercises") val nonPracticedExercises: Int,
    @ColumnInfo(name = "current_streak") val currentStreak: Int
) {
    val percentagePracticedExercises: Int
        get() = (practicedExercises * 100 / totalExercises.toDouble()).roundToInt()
    val successRate: Int
        get() {
            return if (totalAttempts > 0) (correctAttempts * 100 / totalAttempts.toDouble()).roundToInt() else 0
        }
}

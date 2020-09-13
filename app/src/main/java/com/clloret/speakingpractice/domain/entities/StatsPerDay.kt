package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo

data class StatsPerDay(
    @ColumnInfo(name = "day") val day: String,
    @ColumnInfo(name = "total_attempts") val totalAttempts: Int,
    @ColumnInfo(name = "correct_attempts") val correctAttempts: Int,
    @ColumnInfo(name = "incorrect_attempts") val incorrectAttempts: Int
    //@ColumnInfo(name = "success_rate") val successRate: Int
)
//    val percentagePracticedExercises: Int
//        get() = (practicedExercises * 100 / totalExercises.toDouble()).roundToInt()
//}
package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey
    @ColumnInfo(name = "date") val date: LocalDate = LocalDate.now(),
    @ColumnInfo(name = "time_practicing", defaultValue = "0") val timePracticing: Int = 0,
    @ColumnInfo(name = "correct", defaultValue = "0") val correct: Int = 0,
    @ColumnInfo(name = "incorrect", defaultValue = "0") val incorrect: Int = 0,
    @ColumnInfo(name = "total_correct", defaultValue = "0") val totalCorrect: Int = 0,
    @ColumnInfo(name = "total_incorrect", defaultValue = "0") val totalIncorrect: Int = 0,
)

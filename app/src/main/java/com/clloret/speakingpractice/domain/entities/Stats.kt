package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "stats")
data class Stats(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "last_practice_day") val lastPracticeDay: LocalDate,
    @ColumnInfo(name = "current_streak") val currentStreak: Int,
    @ColumnInfo(name = "long_streak") val longStreak: Int,
)

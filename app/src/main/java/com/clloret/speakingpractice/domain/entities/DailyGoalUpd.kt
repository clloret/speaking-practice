package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class DailyGoalUpd(
    @PrimaryKey
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "daily_goal_achieved", defaultValue = "0") val dailyGoalAchieved: Boolean = true
)

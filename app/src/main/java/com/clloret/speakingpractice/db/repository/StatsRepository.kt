package com.clloret.speakingpractice.db.repository

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.entities.DailyStats
import com.clloret.speakingpractice.domain.entities.StatsPerDay
import java.time.LocalDate

class StatsRepository(private val db: AppDatabase) {
    val stats = db.statsDao().getStats()

    fun getStatsPerDay(day: String): LiveData<StatsPerDay> {
        return db.statsDao().getStatsPerDay(day)
    }

    suspend fun getDailyStatsByDate(date: LocalDate): DailyStats {
        return db.statsDao().getDailyStatsByDate(date)
    }

    suspend fun insertDailyStats(dailyStats: DailyStats) {
        db.statsDao().insert(dailyStats)
    }

    suspend fun updateDailyStats(timePracticing: Int) {
        db.statsDao().updateDailyStats(timePracticing)
    }
}

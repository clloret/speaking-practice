package com.clloret.speakingpractice.db.repository

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.entities.DailyStats
import com.clloret.speakingpractice.domain.entities.Stats
import com.clloret.speakingpractice.domain.entities.StatsPerDay
import java.time.LocalDate

class StatsRepository(private val db: AppDatabase) {
    val calculatedStats = db.statsDao().getCalculatedStats()
    val timePracticing = db.statsDao().getTimePracticing()

    fun getStatsPerDay(day: String): LiveData<StatsPerDay> {
        return db.statsDao().getStatsPerDay(day)
    }

    suspend fun getDailyStatsByDate(date: LocalDate): DailyStats? {
        return db.statsDao().getDailyStatsByDate(date)
    }

    fun getDailyStatsFromDate(date: LocalDate): LiveData<List<DailyStats>> {
        return db.statsDao().getDailyStatsFromDate(date)
    }

    suspend fun insertDailyStats(dailyStats: DailyStats) {
        db.statsDao().insert(dailyStats)
    }

    suspend fun updateDailyStats(timePracticing: Int, epochDate: Long) {
        db.statsDao().updateDailyStats(timePracticing, epochDate)
    }

    suspend fun getStats(): Stats? {
        return db.statsDao().getStats()
    }

    suspend fun insertStats(stats: Stats) {
        db.statsDao().insert(stats)
    }
}

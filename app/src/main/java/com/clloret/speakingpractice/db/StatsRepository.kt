package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.domain.entities.StatsPerDay

class StatsRepository(private val db: AppDatabase) {
    val stats = db.statsDao().getStats()

    fun getStatsPerDay(day: String): LiveData<StatsPerDay> {
        return db.statsDao().getStatsPerDay(day)
    }
}

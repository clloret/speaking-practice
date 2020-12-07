package com.clloret.speakingpractice.stats

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.repository.StatsRepository
import java.time.Clock
import java.time.LocalDate

class StatsViewModel(
    repository: StatsRepository,
    clock: Clock
) : ViewModel() {

    companion object {
        const val CHART_NUMBER_OF_DAYS = 6L
    }

    private val lastDay: LocalDate = LocalDate.now(clock)
    private val firstDay: LocalDate = lastDay.minusDays(CHART_NUMBER_OF_DAYS)

    val dailyStats = repository.getDailyStatsFromDate(firstDay)
    val stats = repository.calculatedStats
    val weekDays: List<Int> by lazy {
        val weekRange = firstDay..lastDay
        val weekDays = mutableListOf<Int>()
        for (day in weekRange) {
            weekDays.add(day.dayOfWeek.value)
        }
        weekDays
    }
}

package com.clloret.speakingpractice.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.clloret.speakingpractice.db.repository.StatsRepository
import com.clloret.speakingpractice.utils.TimeUtils
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
    val timePracticing = repository.timePracticing.map { TimeUtils.secondsToTime(it) }
    val weekDays: List<Int> by lazy {
        val weekRange = firstDay..lastDay
        val weekDays = mutableListOf<Int>()
        for (day in weekRange) {
            weekDays.add(day.dayOfWeek.value)
        }
        weekDays
    }

}

operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> {
    return object : Iterator<LocalDate> {
        private var next = this@iterator.start
        private val finalElement = this@iterator.endInclusive
        private var hasNext = !next.isAfter(this@iterator.endInclusive)
        override fun hasNext(): Boolean = hasNext

        override fun next(): LocalDate {
            if (!hasNext) {
                throw NoSuchElementException()
            }

            val value = next

            if (value == finalElement) {
                hasNext = false
            } else {
                next = next.plusDays(1)
            }

            return value
        }
    }
}

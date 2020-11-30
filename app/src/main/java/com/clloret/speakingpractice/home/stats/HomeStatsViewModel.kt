package com.clloret.speakingpractice.home.stats

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.repository.StatsRepository
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

class HomeStatsViewModel(repository: StatsRepository) : ViewModel() {
    val stats = repository.calculatedStats
    val monday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.MONDAY))
    val tuesday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.TUESDAY))
    val wednesday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.WEDNESDAY))
    val thursday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.THURSDAY))
    val friday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.FRIDAY))
    val saturday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.SATURDAY))
    val sunday = repository.getStatsPerDay(getWeekDayFormatted(DayOfWeek.SUNDAY))
    var onClickDayStats: ((String) -> Unit)? = null

    private fun getWeekDayFormatted(dayOfWeek: DayOfWeek): String {
        val date = LocalDate.now().with(dayOfWeek)
        return ISO_LOCAL_DATE.format(date)
    }

    fun showAttempts(dayOfWeek: DayOfWeek) {
        val weekDayFormatted = getWeekDayFormatted(dayOfWeek)
        onClickDayStats?.invoke(weekDayFormatted)
    }

}

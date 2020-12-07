package com.clloret.speakingpractice.stats.formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class DayOfWeekFormatter(private val weekDays: List<Int>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String? {
        val dayOfWeek = weekDays[value.toInt()]
        return DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.SHORT, Locale.US)
    }
}

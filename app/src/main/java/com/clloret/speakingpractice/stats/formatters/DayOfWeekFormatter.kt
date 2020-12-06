package com.clloret.speakingpractice.stats.formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

class DayOfWeekFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String? {
        val intValue = value.toInt()
        return DayOfWeek.of(intValue).getDisplayName(TextStyle.SHORT, Locale.US)
    }
}

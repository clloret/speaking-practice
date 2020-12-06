package com.clloret.speakingpractice.stats.formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat

class CustomPercentFormatter : ValueFormatter() {
    private val format = NumberFormat.getPercentInstance()

    override fun getFormattedValue(value: Float): String? {
        return format.format(value / 100)
    }
}

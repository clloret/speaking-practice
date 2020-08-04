package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import kotlin.math.roundToInt

data class PracticeWordWithResults(
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "correct") val correct: Int,
    @ColumnInfo(name = "incorrect") val incorrect: Int
) {
    val successRate: Int
        get() {
            val count = (correct + incorrect).takeIf { it > 0 } ?: return 0

            return (correct * 100 / count.toDouble()).roundToInt()
        }
}
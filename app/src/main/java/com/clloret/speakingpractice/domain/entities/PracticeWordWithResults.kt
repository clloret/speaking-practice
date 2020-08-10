package com.clloret.speakingpractice.domain.entities

import androidx.room.ColumnInfo
import com.clloret.speakingpractice.domain.word.WordSortable
import kotlin.math.roundToInt

data class PracticeWordWithResults(
    @ColumnInfo(name = "word") override val word: String,
    @ColumnInfo(name = "correct") override val correct: Int,
    @ColumnInfo(name = "incorrect") override val incorrect: Int
) : WordSortable {
    override val successRate: Int
        get() {
            val count = (correct + incorrect).takeIf { it > 0 } ?: return 0

            return (correct * 100 / count.toDouble()).roundToInt()
        }
}
package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.text.color
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.ExerciseValidator.Companion.getWordsWithResults

class FormatCorrectWords {

    companion object {
        fun getFormattedPracticePhrase(
            context: Context,
            practicePhrase: String,
            correctWordsPositions: List<Int>,
            isCurrent: Boolean
        ): Spannable {
            val colorCorrect = ContextCompat.getColor(context, R.color.exercise_correct)
            val colorIncorrect = ContextCompat.getColor(context, R.color.exercise_incorrect)

            if (isCurrent) {
                val spannableBuilder = SpannableStringBuilder()
                val words = getWordsWithResults(practicePhrase, correctWordsPositions)
                words.forEach { word ->
                    val color = if (word.second) colorCorrect else colorIncorrect
                    spannableBuilder.color(color) {
                        append(word.first)
                    }
                }

                return spannableBuilder
            }

            return SpannableString(practicePhrase)
        }

    }
}
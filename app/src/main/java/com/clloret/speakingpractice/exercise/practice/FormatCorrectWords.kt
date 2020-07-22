package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.core.text.color
import com.clloret.speakingpractice.R

class FormatCorrectWords {

    companion object {
        fun getFormattedPracticePhrase(
            context: Context,
            practicePhrase: String,
            correctWords: List<Pair<String, Boolean>>,
            isCurrent: Boolean
        ): Spannable {
            val colorCorrect = ContextCompat.getColor(context, R.color.exercise_correct)
            val colorIncorrect = ContextCompat.getColor(context, R.color.exercise_incorrect)

            if (isCurrent) {
                val spannableBuilder = SpannableStringBuilder()
                correctWords.forEachIndexed { index, pair ->
                    val color = if (pair.second) colorCorrect else colorIncorrect
                    spannableBuilder.color(color) {
                        append(pair.first)
                        if (correctWords.lastIndex != index) {
                            append(' ')
                        }
                    }
                }

                return spannableBuilder
            }

            return SpannableString(practicePhrase)
        }
    }
}
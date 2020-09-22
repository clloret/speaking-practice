package com.clloret.speakingpractice.exercise.practice

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.color
import com.clloret.speakingpractice.domain.resources.ColorResourceProvider

class FormatCorrectWords(private val colorResourceProvider: ColorResourceProvider) {

    fun getFormattedPracticePhrase(
        practicePhrase: String,
        correctWords: List<Pair<String, Boolean>>,
        isCurrent: Boolean
    ): Spanned {
        val colorCorrect = colorResourceProvider.getExerciseCorrect()
        val colorIncorrect = colorResourceProvider.getExerciseIncorrect()

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

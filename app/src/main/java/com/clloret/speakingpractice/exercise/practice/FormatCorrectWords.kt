package com.clloret.speakingpractice.exercise.practice

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.text.color
import com.clloret.speakingpractice.domain.CorrectedWords
import com.clloret.speakingpractice.domain.resources.ColorResourceProvider

class FormatCorrectWords(private val colorResourceProvider: ColorResourceProvider) {
    var onClickWord: ((String) -> Unit)? = null

    fun getFormattedPracticePhrase(
        correctWords: CorrectedWords,
        makeWordsClickable: Boolean = false
    ): Spanned {
        val colorCorrect = colorResourceProvider.getExerciseCorrect()
        val colorIncorrect = colorResourceProvider.getExerciseIncorrect()

        var start = 0
        val spannableBuilder = SpannableStringBuilder()
        correctWords.forEachIndexed { index, pair ->
            val word = pair.first
            val isCorrect = pair.second
            val color = if (isCorrect) colorCorrect else colorIncorrect
            spannableBuilder.color(color) {
                append(word)

                if (makeWordsClickable && !isCorrect) {
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            onClickWord?.invoke(word)
                        }
                    }
                    spannableBuilder.setSpan(
                        clickableSpan,
                        start,
                        start + word.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                if (correctWords.lastIndex != index) {
                    append(' ')
                    start++
                }

                start += word.length
            }

        }

        return spannableBuilder
    }

}

package com.clloret.speakingpractice.exercise.practice

import android.text.SpannedString
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.clloret.speakingpractice.BR
import com.clloret.speakingpractice.domain.CorrectedWords

class ExerciseStatus(private val formatCorrectWords: FormatCorrectWords) : BaseObservable() {
    data class CorrectedTexts(
        val exercise: CorrectedWords = emptyList(),
        val recognized: CorrectedWords = emptyList()
    ) {
        companion object {
            val EMPTY = CorrectedTexts()
        }
    }

    private var exerciseText: String? = null

    private val correctedWords
        get() = if (showRecognizedText) correctedTexts.recognized else correctedTexts.exercise

    private val textToRender
        get() = if (showRecognizedText) recognizedText else exerciseText

    var recognizedText: String? = null
    var correctedTexts = CorrectedTexts()

    @get:Bindable
    var showRecognizedText = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.showRecognizedText)
            notifyPropertyChanged(BR.practicePhrase)
        }

    @get:Bindable
    var isCorrected = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.corrected)
        }

    @get:Bindable
    val practicePhrase
        get() =
            if (isCorrected)
                formatCorrectWords.getFormattedPracticePhrase(
                    correctedWords
                )
            else SpannedString(textToRender)

    fun saveCorrectedExercise(
        exerciseWords: CorrectedWords,
        recognizedWords: CorrectedWords,
        recognizedText: String
    ) {
        this.recognizedText = recognizedText
        correctedTexts = CorrectedTexts(exerciseWords, recognizedWords)
        isCorrected = true
    }

    fun resetExercise(exerciseText: String) {
        this.exerciseText = exerciseText
        recognizedText = null
        correctedTexts = CorrectedTexts.EMPTY
        showRecognizedText = false
        isCorrected = false
        notifyPropertyChanged(BR.practicePhrase)
    }

}

package com.clloret.speakingpractice.attempt.list

import android.text.Spanned
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterStrategy
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.exercise.practice.FormatCorrectWords

class AttemptListViewModel(
    filter: AttemptFilterStrategy,
    repository: AppRepository,
    private val formatCorrectWords: FormatCorrectWords
) : ViewModel() {

    val attempts = filter.get(repository)

    fun getFormattedPracticePhrase(exercise: Exercise, attempt: ExerciseAttempt): Spanned {
        val correctWords = ExerciseValidator.getWordsWithResults(
            attempt.recognizedText,
            exercise.practicePhrase
        )

        return formatCorrectWords.getFormattedPracticePhrase(
            exercise.practicePhrase,
            correctWords,
            true
        )
    }

}

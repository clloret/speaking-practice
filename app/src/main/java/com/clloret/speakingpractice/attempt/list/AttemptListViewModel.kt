package com.clloret.speakingpractice.attempt.list

import android.app.Application
import android.text.Spannable
import androidx.lifecycle.AndroidViewModel
import com.clloret.speakingpractice.App
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.exercise.practice.FormatCorrectWords

class AttemptListViewModel(
    application: Application,
    repository: AppRepository,
    exerciseId: Int
) : AndroidViewModel(application) {

    val attempts = repository.getExerciseAttemptsByExerciseId(exerciseId)

    fun getFormattedPracticePhrase(exercise: Exercise, attempt: ExerciseAttempt): Spannable {
        val context = getApplication<App>().applicationContext

        val correctWords = ExerciseValidator.getWordsWithResults(
            attempt.recognizedText,
            exercise.practicePhrase
        )

        return FormatCorrectWords.getFormattedPracticePhrase(
            context,
            exercise.practicePhrase,
            correctWords,
            true
        )
    }

}

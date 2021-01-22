package com.clloret.speakingpractice.attempt.list

import android.text.Spanned
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.attempt.criteria.AttemptCriteriaByResult
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterStrategy
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.exercise.practice.FormatCorrectWords

class AttemptListViewModel(
    filter: AttemptFilterStrategy,
    repository: AttemptRepository,
    private val formatCorrectWords: FormatCorrectWords
) : ViewModel() {

    private var unfilteredData = emptyList<AttemptWithExercise>()

    val attempts = MediatorLiveData<List<AttemptWithExercise>>()

    init {
        attempts.addSource(filter.get(repository)) {
            unfilteredData = it

            val meetCriteria = AttemptCriteriaByResult(AttemptCriteriaByResult.Result.INDISTINCT)
                .meetCriteria(unfilteredData)
            attempts.postValue(meetCriteria)
        }
    }

    fun filterByResult(result: AttemptCriteriaByResult.Result) {
        val meetCriteria = AttemptCriteriaByResult(result).meetCriteria(unfilteredData)
        attempts.postValue(meetCriteria)
    }

    fun getFormattedPracticePhrase(exercise: Exercise, attempt: ExerciseAttempt): Spanned {
        val correctWords = ExerciseValidator.getWordsWithResults(
            attempt.recognizedText,
            exercise.practicePhrase
        )

        return formatCorrectWords.getFormattedPracticePhrase(correctWords)
    }

}

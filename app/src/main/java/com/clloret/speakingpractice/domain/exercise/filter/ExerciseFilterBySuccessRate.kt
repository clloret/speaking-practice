package com.clloret.speakingpractice.domain.exercise.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.runBlocking

class ExerciseFilterBySuccessRate(
    private val successFactor: Double = DEFAULT_SUCCESS_FACTOR,
    private val minAttempts: Int = DEFAULT_MIN_ATTEMPTS
) :
    ExerciseFilterStrategy() {

    override fun getExercises(repository: AppRepository): LiveData<List<ExerciseWithDetails>> {
        val ids = runBlocking {
            repository.getMostFailedExercisesIds(successFactor, minAttempts)
        }
        return repository.getExercisesDetailsByIds(ids)
    }

    companion object {
        const val DEFAULT_SUCCESS_FACTOR = 0.80
        const val DEFAULT_MIN_ATTEMPTS = 10
    }
}

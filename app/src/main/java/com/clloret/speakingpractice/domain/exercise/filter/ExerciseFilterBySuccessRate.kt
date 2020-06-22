package com.clloret.speakingpractice.domain.exercise.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.runBlocking

class ExerciseFilterBySuccessRate(private val successFactor: Double, private val minAttempts: Int) :
    ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseWithDetails>> {
        val ids = runBlocking {
            repository.getMostFailedExercisesIds(successFactor, minAttempts)
        }
        return repository.getExercisesDetailsByIds(ids)
    }
}

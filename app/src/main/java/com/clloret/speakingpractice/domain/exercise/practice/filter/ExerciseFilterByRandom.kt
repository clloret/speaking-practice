package com.clloret.speakingpractice.domain.exercise.practice.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.runBlocking

class ExerciseFilterByRandom(private val limit: Int) : ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseWithDetails>> {
        val ids = runBlocking {
            repository.getRandomExercisesIds(limit)
        }
        return repository.getExercisesDetailsByIds(ids)
    }
}

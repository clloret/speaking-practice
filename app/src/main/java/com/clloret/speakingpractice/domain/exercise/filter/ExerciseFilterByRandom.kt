package com.clloret.speakingpractice.domain.exercise.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseDetail
import kotlinx.coroutines.runBlocking

class ExerciseFilterByRandom(private val limit: Int) : ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseDetail>> {
        val ids = runBlocking {
            repository.getRandomIds(limit)
        }
        return repository.getExercisesDetailsByIds(ids)
    }
}

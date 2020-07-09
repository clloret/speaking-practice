package com.clloret.speakingpractice.domain.exercise.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.runBlocking

class ExerciseFilterByLessPracticed(private val limit: Int) :
    ExerciseFilterStrategy() {

    override fun getExercises(repository: AppRepository): LiveData<List<ExerciseWithDetails>> {
        val ids = runBlocking {
            repository.getLessPracticedExercisesIds(limit)
        }
        return repository.getExercisesDetailsByIds(ids)
    }
}

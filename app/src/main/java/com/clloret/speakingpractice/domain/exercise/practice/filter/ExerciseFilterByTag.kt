package com.clloret.speakingpractice.domain.exercise.practice.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import kotlinx.coroutines.runBlocking

class ExerciseFilterByTag(private val tagId: Int) : ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseWithDetails>> {
        val ids = runBlocking {
            repository.getExercisesIdsByTag(tagId)
        }
        return repository.getExercisesDetailsByIds(ids)
    }
}

package com.clloret.speakingpractice.domain.exercise.practice.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.AppRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class ExerciseFilterById(private val exerciseId: Int) : ExerciseFilterStrategy() {

    override fun getExercises(repository: AppRepository): LiveData<List<ExerciseWithDetails>> {
        val ids = listOf(exerciseId)
        return repository.getExercisesDetailsByIds(ids)
    }
}

package com.clloret.speakingpractice.domain.exercise.practice.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class ExerciseFilterAll : ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseWithDetails>> {
        return repository.allExercisesDetails
    }
}

package com.clloret.speakingpractice.domain.exercise.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class ExerciseFilterAll : ExerciseFilterStrategy() {

    override fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseWithDetails>> {
        return repository.allExercisesDetails
    }
}

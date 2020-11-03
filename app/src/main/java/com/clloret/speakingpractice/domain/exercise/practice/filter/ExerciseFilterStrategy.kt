package com.clloret.speakingpractice.domain.exercise.practice.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.AppRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import java.io.Serializable

abstract class ExerciseFilterStrategy : Serializable {
    abstract fun getExercises(repository: AppRepository): LiveData<List<ExerciseWithDetails>>
}

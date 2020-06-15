package com.clloret.speakingpractice.domain.exercise.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.domain.entities.ExerciseDetail
import java.io.Serializable

abstract class ExerciseFilterStrategy : Serializable {
    abstract fun getExercises(repository: ExerciseRepository): LiveData<List<ExerciseDetail>>
}
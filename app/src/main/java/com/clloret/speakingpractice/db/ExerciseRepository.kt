package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.exercise.Exercise

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()

    suspend fun insert(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }
}
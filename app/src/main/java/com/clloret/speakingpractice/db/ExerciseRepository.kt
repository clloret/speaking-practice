package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.exercise.Exercise

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExerciseById(id: Int): LiveData<Exercise> {
        return exerciseDao.getExerciseById(id)
    }

    suspend fun insert(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }

    suspend fun deleteAll() {
        exerciseDao.deleteAll()
    }

}
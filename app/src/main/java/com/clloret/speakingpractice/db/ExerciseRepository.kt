package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt

class ExerciseRepository(private val db: ExercisesDatabase) {

    val allExercises: LiveData<List<Exercise>> = db.exerciseDao().getAllExercises()

    fun getExerciseById(id: Int): LiveData<Exercise> {
        return db.exerciseDao().getExerciseById(id)
    }

    suspend fun insertExercise(exercise: Exercise) {
        db.exerciseDao().insert(exercise)
    }

    suspend fun updateExercise(exercise: Exercise) {
        db.exerciseDao().update(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        db.exerciseDao().delete(exercise)
    }

    suspend fun deleteAllExercises() {
        db.exerciseDao().deleteAll()
    }

    suspend fun insertAttempt(exerciseResult: ExerciseAttempt) {
        db.exerciseAttemptDao().insert(exerciseResult)
    }

}
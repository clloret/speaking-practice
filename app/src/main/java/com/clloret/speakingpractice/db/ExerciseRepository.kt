package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.ExerciseDetail
import com.clloret.speakingpractice.domain.entities.ExerciseResultTuple

class ExerciseRepository(private val db: ExercisesDatabase) {

    val allExercises: LiveData<List<Exercise>> = db.exerciseDao().getAllExercises()

    val allExercisesDetails: LiveData<List<ExerciseDetail>> =
        db.exerciseDao().getAllExercisesDetail()

    fun getExerciseById(id: Int): LiveData<Exercise> {
        return db.exerciseDao().getExerciseById(id)
    }

    fun getResultValues(exerciseId: Int): LiveData<ExerciseResultTuple> {
        return db.exerciseDao().getResultValues(exerciseId)
    }

    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<ExerciseAttempt>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(id)
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

    suspend fun deleteExerciseById(exerciseId: Int) {
        db.exerciseDao().deleteById(exerciseId)
    }

    suspend fun deleteAllExercises() {
        db.exerciseDao().deleteAll()
    }

    suspend fun insertAttempt(exerciseResult: ExerciseAttempt) {
        db.exerciseAttemptDao().insert(exerciseResult)
    }

}
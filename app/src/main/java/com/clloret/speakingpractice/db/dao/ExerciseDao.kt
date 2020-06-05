package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData

import androidx.room.*
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseDetail
import com.clloret.speakingpractice.domain.entities.ExerciseResultTuple

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT * FROM ExerciseDetail")
    fun getAllExercisesDetail(): LiveData<List<ExerciseDetail>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: Int): LiveData<Exercise>

    @Query("SELECT SUM(result) AS correct, COUNT(*) - SUM(result) AS incorrect FROM exercises INNER JOIN exercise_attempts ON exercises.id = exercise_attempts.exercise_id GROUP BY exercise_id HAVING exercise_id=:exerciseId")
    fun getResultValues(exerciseId: Int): LiveData<ExerciseResultTuple>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}
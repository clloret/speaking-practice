package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData

import androidx.room.*
import com.clloret.speakingpractice.exercise.Exercise

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: Int): LiveData<Exercise>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}
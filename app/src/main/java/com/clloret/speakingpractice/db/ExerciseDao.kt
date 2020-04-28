package com.clloret.speakingpractice.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clloret.speakingpractice.exercise.Exercise

@Dao
interface ExerciseDao {

    @Query("SELECT * from exercises")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise)

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}
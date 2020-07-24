package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt

@Dao
interface ExerciseAttemptDao {
    @Transaction
    @Query("SELECT * FROM exercise_attempts WHERE exercise_id = :id ORDER BY time DESC")
    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<AttemptWithExercise>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exerciseResult: ExerciseAttempt)
}
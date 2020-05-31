package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt

@Dao
interface ExerciseAttemptDao {
    @Query("SELECT * FROM exercise_attempts WHERE exercise_id = :id ORDER BY time DESC")
    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<ExerciseAttempt>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exerciseResult: ExerciseAttempt)
}
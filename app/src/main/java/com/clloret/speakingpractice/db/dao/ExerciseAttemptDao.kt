package com.clloret.speakingpractice.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt

@Dao
interface ExerciseAttemptDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exerciseResult: ExerciseAttempt)
}
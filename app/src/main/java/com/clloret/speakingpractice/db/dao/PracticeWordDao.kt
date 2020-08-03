package com.clloret.speakingpractice.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.clloret.speakingpractice.domain.entities.PracticeWord

@Dao
interface PracticeWordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(practiceWord: PracticeWord)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(practiceWords: List<PracticeWord>)
}
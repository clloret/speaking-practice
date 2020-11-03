package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.clloret.speakingpractice.domain.entities.PracticeWord
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults

@Dao
interface PracticeWordDao {
    @Query(
        """
                SELECT
                    word,
                    SUM(result) AS correct,
                    COUNT() - SUM(result) AS incorrect
                FROM 
                    practice_words
                GROUP BY word;
    """
    )
    fun getPracticeWordsWithResults(): LiveData<List<PracticeWordWithResults>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(practiceWords: List<PracticeWord>)
}

package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.clloret.speakingpractice.domain.entities.PracticeWord
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults

@Dao
interface PracticeWordDao {
    @Query(
        """
                SELECT word,
                       SUM(result) AS correct,
                       COUNT() - SUM(result) AS incorrect
                  FROM practice_words
                 GROUP BY word;
    """
    )
    fun getPracticeWordsWithResults(): LiveData<List<PracticeWordWithResults>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(practiceWords: List<PracticeWord>)

    @Query("DELETE FROM practice_words WHERE exercise_attempt_id = :id")
    suspend fun deleteWordsByExerciseAttemptId(id: Int)

    @Transaction
    suspend fun replaceAllWordsByExerciseAttemptId(id: Int, practiceWords: List<PracticeWord>) {
        deleteWordsByExerciseAttemptId(id)
        insertAll(practiceWords)
    }

}

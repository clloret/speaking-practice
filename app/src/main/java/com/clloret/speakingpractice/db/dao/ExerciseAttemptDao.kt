package com.clloret.speakingpractice.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.PracticeWord

@Dao
interface ExerciseAttemptDao {
    @Transaction
    @Query("SELECT * FROM exercise_attempts WHERE exercise_id = :id ORDER BY time DESC")
    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<AttemptWithExercise>>

    @Transaction
    @Query("SELECT * FROM exercise_attempts WHERE exercise_attempt_id IN (:ids) ORDER BY time DESC")
    fun getExerciseAttemptsByIds(ids: List<Int>): LiveData<List<AttemptWithExercise>>

    @Transaction
    @Query(
        """
                SELECT *
                  FROM exercise_attempts
                 WHERE DATE(time / 1000, 'unixepoch') = :date;
    """
    )
    fun getExerciseAttemptsByDay(date: String): LiveData<List<AttemptWithExercise>>

    @Query("SELECT exercise_attempt_id FROM practice_words WHERE word = :practiceWord")
    suspend fun getExercisesAttemptsIdsByWord(practiceWord: String): List<Int>

    @Query("SELECT exercise_attempt_id FROM practice_words WHERE word = :practiceWord AND result = 0")
    suspend fun getExercisesAttemptsIdsByWordIncorrect(practiceWord: String): List<Int>

    @Query("SELECT COUNT() FROM exercise_attempts")
    fun getExercisesAttemptsCount(): LiveData<Int>

    @Query("SELECT COUNT() FROM exercise_attempts WHERE DATE(time / 1000, 'unixepoch') = :day")
    suspend fun getTotalAttemptsByDay(day: String): Int

    @Query(
        """
                SELECT exercise_attempt_id
                  FROM exercise_attempts
                 WHERE exercise_id = :exerciseId
                 ORDER BY time DESC
                 LIMIT 1;
    """
    )
    suspend fun getLastExerciseAttemptId(exerciseId: Int): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exerciseAttempt: ExerciseAttempt): Long

    @Transaction
    suspend fun insertExerciseAttemptAndWords(
        exerciseAttempt: ExerciseAttempt,
        practiceWords: List<PracticeWord>,
        practiceWordDao: PracticeWordDao,
        replaceAttempt: Boolean
    ) {
        if (replaceAttempt) {
            getLastExerciseAttemptId(exerciseAttempt.exerciseId)?.let {
                exerciseAttempt.id = it
            }
        }

        val exerciseAttemptId = insert(exerciseAttempt).toInt()
        val wordsWithId = practiceWords.map {
            PracticeWord(
                exerciseAttemptId = exerciseAttemptId,
                word = it.word,
                result = it.result
            )
        }
        practiceWordDao.replaceAllWordsByExerciseAttemptId(exerciseAttemptId, wordsWithId)
    }

    @Query("DELETE FROM exercise_attempts WHERE exercise_attempt_id = :id")
    suspend fun deleteById(id: Int)

}

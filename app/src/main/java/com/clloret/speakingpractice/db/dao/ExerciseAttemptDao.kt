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

    @Query("SELECT exercise_attempt_id FROM practice_words WHERE word = :practiceWord")
    suspend fun getExercisesAttemptsIdsByWord(practiceWord: String): List<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exerciseAttempt: ExerciseAttempt): Long

    @Transaction
    suspend fun insertExerciseAttemptAndWords(
        exerciseAttempt: ExerciseAttempt,
        practiceWords: List<PracticeWord>,
        practiceWordDao: PracticeWordDao
    ) {
        val exerciseAttemptId = insert(exerciseAttempt).toInt()
        val wordsWithId = practiceWords.map {
            PracticeWord(
                exerciseAttemptId = exerciseAttemptId,
                word = it.word,
                result = it.result
            )
        }
        practiceWordDao.insertAll(wordsWithId)
    }

}
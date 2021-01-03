package com.clloret.speakingpractice.db.repository

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.PracticeWord

class AttemptRepository(private val db: AppDatabase) {
    val exerciseAttemptsCount = db.exerciseAttemptDao().getExercisesAttemptsCount()

    fun getExerciseAttemptsByIds(ids: List<Int>): LiveData<List<AttemptWithExercise>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByIds(ids)
    }

    fun getExerciseAttemptsByExerciseId(id: Int): LiveData<List<AttemptWithExercise>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(id)
    }

    fun getExerciseAttemptsByDay(day: String): LiveData<List<AttemptWithExercise>> {
        return db.exerciseAttemptDao().getExerciseAttemptsByDay(day)
    }

    suspend fun getTotalAttemptsByDay(day: String): Int {
        return db.exerciseAttemptDao().getTotalAttemptsByDay(day)
    }

    suspend fun getExercisesAttemptsIdsByWord(practiceWord: String): List<Int> {
        return db.exerciseAttemptDao().getExercisesAttemptsIdsByWord(practiceWord)
    }

    suspend fun insertExerciseAttemptAndWords(
        exerciseAttempt: ExerciseAttempt,
        practiceWords: List<PracticeWord>
    ) {
        db.exerciseAttemptDao()
            .insertExerciseAttemptAndWords(exerciseAttempt, practiceWords, db.practiceWordDao())
    }

}

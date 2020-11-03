package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AttemptRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import kotlinx.coroutines.runBlocking

class AttemptFilterByWord(private val practiceWord: String) : AttemptFilterStrategy() {
    override fun get(repository: AttemptRepository): LiveData<List<AttemptWithExercise>> {
        val ids = runBlocking {
            repository.getExercisesAttemptsIdsByWord(practiceWord)
        }
        return repository.getExerciseAttemptsByIds(ids)
    }
}

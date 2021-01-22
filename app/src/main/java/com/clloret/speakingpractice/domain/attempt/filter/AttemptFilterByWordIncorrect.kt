package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import kotlinx.coroutines.runBlocking

class AttemptFilterByWordIncorrect(private val practiceWord: String) : AttemptFilterStrategy() {
    override fun get(repository: AttemptRepository): LiveData<List<AttemptWithExercise>> {
        val ids = runBlocking {
            repository.getExercisesAttemptsIdsByWordIncorrect(practiceWord)
        }
        return repository.getExerciseAttemptsByIds(ids)
    }
}

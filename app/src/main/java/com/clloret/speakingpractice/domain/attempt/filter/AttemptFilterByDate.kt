package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise

class AttemptFilterByDate(private val date: String) : AttemptFilterStrategy() {

    override fun get(repository: AttemptRepository): LiveData<List<AttemptWithExercise>> {
        return repository.getExerciseAttemptsByDay(date)
    }
}

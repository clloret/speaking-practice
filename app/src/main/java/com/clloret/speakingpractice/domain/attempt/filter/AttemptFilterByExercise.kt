package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AttemptRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise

class AttemptFilterByExercise(private val exerciseId: Int) : AttemptFilterStrategy() {

    override fun get(repository: AttemptRepository): LiveData<List<AttemptWithExercise>> {
        return repository.getExerciseAttemptsByExerciseId(exerciseId)
    }
}

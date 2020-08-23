package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise

class AttemptFilterByExercise(private val exerciseId: Int) : AttemptFilterStrategy() {

    override fun get(repository: AppRepository): LiveData<List<AttemptWithExercise>> {
        return repository.getExerciseAttemptsByExerciseId(exerciseId)
    }
}

package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import java.io.Serializable

abstract class AttemptFilterStrategy : Serializable {
    abstract fun get(repository: AttemptRepository): LiveData<List<AttemptWithExercise>>
}

package com.clloret.speakingpractice.domain.attempt.filter

import androidx.lifecycle.LiveData
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import java.io.Serializable

abstract class AttemptFilterStrategy : Serializable {
    abstract fun get(repository: AppRepository): LiveData<List<AttemptWithExercise>>
}
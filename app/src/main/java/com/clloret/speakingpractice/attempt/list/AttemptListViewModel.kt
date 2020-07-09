package com.clloret.speakingpractice.attempt.list

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository

class AttemptListViewModel(
    repository: AppRepository,
    exerciseId: Int
) : ViewModel() {

    val attempts = repository.getExerciseAttemptsByExerciseId(exerciseId)
}

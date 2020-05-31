package com.clloret.speakingpractice.attempt.list

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.ExerciseRepository

class AttemptListViewModel(
    repository: ExerciseRepository,
    exerciseId: Int
) : ViewModel() {

    val attempts = repository.getExerciseAttemptsByExerciseId(exerciseId)
}

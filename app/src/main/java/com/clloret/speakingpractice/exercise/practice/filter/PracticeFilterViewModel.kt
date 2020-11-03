package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AttemptRepository

class PracticeFilterViewModel(repository: AttemptRepository) : ViewModel() {
    val exerciseAttemptsCount = repository.exerciseAttemptsCount
}

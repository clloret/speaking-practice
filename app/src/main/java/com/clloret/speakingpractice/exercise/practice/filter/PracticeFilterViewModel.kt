package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository

class PracticeFilterViewModel(repository: AppRepository) : ViewModel() {
    val exerciseAttemptsCount = repository.exerciseAttemptsCount
}

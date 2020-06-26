package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.ExerciseRepository

class SelectTagDlgViewModel(repository: ExerciseRepository) : ViewModel() {

    val tags = repository.allTags
}
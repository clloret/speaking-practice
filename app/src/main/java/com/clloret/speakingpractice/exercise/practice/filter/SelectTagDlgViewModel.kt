package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository

class SelectTagDlgViewModel(repository: AppRepository) : ViewModel() {

    val tags = repository.allTags
}
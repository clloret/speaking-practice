package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.TagRepository

class SelectTagDlgViewModel(repository: TagRepository) : ViewModel() {

    val tags = repository.allTags
}

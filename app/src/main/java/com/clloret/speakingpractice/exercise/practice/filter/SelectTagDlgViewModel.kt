package com.clloret.speakingpractice.exercise.practice.filter

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.repository.TagRepository

class SelectTagDlgViewModel(repository: TagRepository) : ViewModel() {

    val tags = repository.allTags
}

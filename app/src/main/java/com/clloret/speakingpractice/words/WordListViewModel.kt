package com.clloret.speakingpractice.words

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository

class WordListViewModel(repository: AppRepository) : ViewModel() {
    val words = repository.allPracticeWords
}
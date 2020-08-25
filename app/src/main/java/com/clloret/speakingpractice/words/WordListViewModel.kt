package com.clloret.speakingpractice.words

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.word.sort.WordSortable

class WordListViewModel(repository: AppRepository) : ViewModel() {
    val words = repository.allPracticeWords
    var selectedComparator: Comparator<WordSortable>? = null
    var sortItemId: Int? = null
}
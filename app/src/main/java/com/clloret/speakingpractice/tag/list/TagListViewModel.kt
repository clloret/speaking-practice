package com.clloret.speakingpractice.tag.list

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.ExerciseRepository
import kotlinx.coroutines.runBlocking

class TagListViewModel(private val repository: ExerciseRepository) : ViewModel() {

    val tags = repository.allTags

    fun deleteTagList(list: List<Int>) {
        runBlocking {
            repository.deleteTagList(list)
        }
    }
}

package com.clloret.speakingpractice.tag.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.AppRepository
import kotlinx.coroutines.launch

class TagListViewModel(private val repository: AppRepository) : ViewModel() {

    val tags = repository.allTags

    fun deleteTagList(list: List<Int>) {
        viewModelScope.launch {
            repository.deleteTagList(list)
        }
    }
}

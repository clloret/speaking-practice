package com.clloret.speakingpractice.tag.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.clloret.speakingpractice.db.ExerciseRepository
import kotlinx.coroutines.runBlocking

class TagListViewModel(application: Application, private val repository: ExerciseRepository) :
    AndroidViewModel(application) {

    val tags = repository.allTags

    fun deleteTagList(list: List<Int>) {
        runBlocking {
            repository.deleteTagList(list)
        }
    }
}

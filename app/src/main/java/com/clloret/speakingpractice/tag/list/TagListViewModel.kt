package com.clloret.speakingpractice.tag.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.App
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import kotlinx.coroutines.runBlocking

class TagListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    val tags = repository.allTags

    private fun initRepository(): ExerciseRepository {
        val application = getApplication<App>()
        val db = ExercisesDatabase.getDatabase(application, viewModelScope)
        return ExerciseRepository(db)
    }

    fun deleteTagList(list: List<Int>) {
        runBlocking {
            repository.deleteTagList(list)
        }
    }
}

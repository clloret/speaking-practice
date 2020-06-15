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

//    var exerciseTags: ObservableField<List<ChipBindingEntry>> = ObservableField()

    val tags = repository.allTags

    init {
//        repository.allTags.apply {
//            observeForever { value ->
//                value?.let {
//                    exerciseTags.set(it)
//                }
//            }
//        }
    }

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

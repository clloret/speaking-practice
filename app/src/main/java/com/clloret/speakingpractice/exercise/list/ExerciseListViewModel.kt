package com.clloret.speakingpractice.exercise.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.App
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase

class ExerciseListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    val exercises = repository.allExercisesDetails

    private fun initRepository(): ExerciseRepository {
        val application = getApplication<App>()
        val db = ExercisesDatabase.getDatabase(application, viewModelScope)
        return ExerciseRepository(db)
    }
}

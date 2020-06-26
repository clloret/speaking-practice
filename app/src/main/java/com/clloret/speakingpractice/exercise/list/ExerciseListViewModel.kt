package com.clloret.speakingpractice.exercise.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.clloret.speakingpractice.db.ExerciseRepository
import kotlinx.coroutines.runBlocking

class ExerciseListViewModel(application: Application, private val repository: ExerciseRepository) :
    AndroidViewModel(application) {

    val exercises = repository.allExercisesDetails

    fun deleteExerciseList(list: List<Int>) {
        runBlocking {
            repository.deleteExerciseList(list)
        }
    }
}

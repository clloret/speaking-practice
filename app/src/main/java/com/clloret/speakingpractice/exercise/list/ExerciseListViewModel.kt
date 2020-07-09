package com.clloret.speakingpractice.exercise.list

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository
import kotlinx.coroutines.runBlocking

class ExerciseListViewModel(private val repository: AppRepository) : ViewModel() {

    val exercises = repository.allExercisesDetails

    fun deleteExerciseList(list: List<Int>) {
        runBlocking {
            repository.deleteExerciseList(list)
        }
    }
}

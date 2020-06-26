package com.clloret.speakingpractice.exercise.list

import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.ExerciseRepository
import kotlinx.coroutines.runBlocking

class ExerciseListViewModel(private val repository: ExerciseRepository) : ViewModel() {

    val exercises = repository.allExercisesDetails

    fun deleteExerciseList(list: List<Int>) {
        runBlocking {
            repository.deleteExerciseList(list)
        }
    }
}

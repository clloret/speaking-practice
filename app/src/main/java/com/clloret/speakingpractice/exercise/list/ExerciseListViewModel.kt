package com.clloret.speakingpractice.exercise.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.AppRepository
import kotlinx.coroutines.launch

class ExerciseListViewModel(private val repository: AppRepository) : ViewModel() {

    val exercises = repository.allExercisesDetails

    fun deleteExerciseList(list: List<Int>) {
        viewModelScope.launch {
            repository.deleteExerciseList(list)
        }
    }
}

package com.clloret.speakingpractice.exercise.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.exercise.sort.ExerciseSortable
import kotlinx.coroutines.launch

class ExerciseListViewModel(private val repository: AppRepository) : ViewModel() {

    val exercises = repository.allExercisesDetails
    var selectedComparator: Comparator<ExerciseSortable>? = null
    var sortItemId: Int? = null

    fun deleteExerciseList(list: List<Int>) {
        viewModelScope.launch {
            repository.deleteExerciseList(list)
        }
    }
}

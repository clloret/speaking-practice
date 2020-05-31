package com.clloret.speakingpractice.attempt.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clloret.speakingpractice.db.ExerciseRepository

class AttemptListViewModelFactory(
    private val repository: ExerciseRepository,
    private val exerciseId: Int
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AttemptListViewModel::class.java)) {
            AttemptListViewModel(repository, exerciseId) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
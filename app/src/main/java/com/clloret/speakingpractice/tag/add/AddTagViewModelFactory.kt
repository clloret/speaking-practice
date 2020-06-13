package com.clloret.speakingpractice.tag.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clloret.speakingpractice.db.ExerciseRepository

class AddTagViewModelFactory(
    private val repository: ExerciseRepository,
    private val exerciseId: Int
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddTagViewModel::class.java)) {
            AddTagViewModel(repository, exerciseId) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
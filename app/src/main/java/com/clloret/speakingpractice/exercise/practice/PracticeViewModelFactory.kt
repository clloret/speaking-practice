package com.clloret.speakingpractice.exercise.practice

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy

class PracticeViewModelFactory(
    private val application: Application,
    private val filter: ExerciseFilterStrategy
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(PracticeViewModel::class.java)) {
            PracticeViewModel(application, filter) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
    }
}
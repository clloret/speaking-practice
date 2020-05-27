package com.clloret.speakingpractice.exercise.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clloret.speakingpractice.db.ExerciseRepository

class AddExerciseViewModelFactory(
    private val repository: ExerciseRepository,
    private val exerciseId: Int
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AddExerciseViewModel::class.java)) {
            AddExerciseViewModel(repository, exerciseId) as T
        } else {
            throw IllegalArgumentException("ViewModel not found")
        }
        //return AddExerciseViewModel(db, exerciseId) as T
//        return modelClass.getConstructor(ExercisesDatabase::class.java, Int::class.java)
//            .newInstance(db, exerciseId)
    }
}
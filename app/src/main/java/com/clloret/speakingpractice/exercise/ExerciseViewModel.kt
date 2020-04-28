package com.clloret.speakingpractice.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import timber.log.Timber


class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository

    var phrases: List<Exercise> = listOf()
        set(value) {
            field = value
            phrasesIterator = phrases.listIterator()
        }
    private var phrasesIterator: ListIterator<Exercise> = phrases.listIterator()

    val allExercises: LiveData<List<Exercise>>
    val currentExercise: MutableLiveData<Exercise> by lazy {
        MutableLiveData<Exercise>()
    }

    init {
        val exerciseDao = ExercisesDatabase.getDatabase(application, viewModelScope).exerciseDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
    }

    fun loadNextExercise() {
        Timber.d("loadNextExercise")

        if (phrasesIterator.hasNext()) {
            currentExercise.value = phrasesIterator.next()
        }
    }

    fun loadPreviousExercise() {
        Timber.d("loadPreviousExercise")

        if (phrasesIterator.hasPrevious()) {
            currentExercise.value = phrasesIterator.previous()
        }
    }

    fun validatePhrase(text: String): Boolean {
        return currentExercise.value?.practicePhrase.equals(text, true)
    }

}

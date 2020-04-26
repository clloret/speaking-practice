package com.clloret.speakingpractice.exercise

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class ExerciseViewModel : ViewModel() {
    private val phrases = listOf(
        Exercise("What is your name", "¿Cómo te llamas?"),
        Exercise("What do you do", "¿Qué haces?"),
        Exercise("How old are you", "¿Cuantos años tienes?")
    )
    private val phrasesIterator = phrases.listIterator()

    fun loadNextExercise() {
        Timber.d("loadNextExercise")

        if (phrasesIterator.hasNext()) {
            exercise.value = phrasesIterator.next()
        }
    }

    fun loadPreviousExercise() {
        Timber.d("loadPreviousExercise")

        if (phrasesIterator.hasPrevious()) {
            exercise.value = phrasesIterator.previous()
        }
    }

    fun validatePhrase(text: String): Boolean {
        return exercise.value?.practicePhrase.equals(text, true)
    }

    val exercise: MutableLiveData<Exercise> by lazy {
        MutableLiveData<Exercise>()
    }

}

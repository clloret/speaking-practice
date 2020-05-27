package com.clloret.speakingpractice.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.clloret.speakingpractice.domain.ExerciseValidator
import kotlinx.coroutines.runBlocking
import timber.log.Timber


class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    enum class ExerciseResult {
        HIDDEN, CORRECT, INCORRECT
    }

    private val repository: ExerciseRepository

    var exerciseResult: MutableLiveData<ExerciseResult> = MutableLiveData(ExerciseResult.HIDDEN)
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
    val speakText: MutableLiveData<String> = MutableLiveData()

    init {
        val exerciseDao = ExercisesDatabase.getDatabase(application, viewModelScope).exerciseDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
        loadNextExercise()
    }

    fun loadNextExercise() {
        Timber.d("loadNextExercise")

        if (phrasesIterator.hasNext()) {
            currentExercise.value = phrasesIterator.next()
        }

        exerciseResult.postValue(ExerciseResult.HIDDEN)
    }

    fun loadPreviousExercise() {
        Timber.d("loadPreviousExercise")

        if (phrasesIterator.hasPrevious()) {
            currentExercise.value = phrasesIterator.previous()
        }

        exerciseResult.postValue(ExerciseResult.HIDDEN)
    }

    fun validatePhrase(text: String) {
        val result =
            currentExercise.value?.practicePhrase?.let {
                ExerciseValidator.validatePhrase(
                    text,
                    it
                )
            }
        exerciseResult.postValue(if (result!!) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)
    }

    fun speakText() {
        speakText.postValue(currentExercise.value?.practicePhrase)
    }

    fun deleteCurrentExercise() {
        currentExercise.value?.let {
            runBlocking {
                repository.delete(it)
            }
        }
    }

}

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

    private var _exerciseResult: MutableLiveData<ExerciseResult> =
        MutableLiveData(ExerciseResult.HIDDEN)
    val exerciseResult: LiveData<ExerciseResult> get() = _exerciseResult

    private val _currentExercise: MutableLiveData<Exercise> by lazy {
        MutableLiveData<Exercise>()
    }
    val currentExercise: LiveData<Exercise> get() = _currentExercise

    private val _speakText: MutableLiveData<String> = MutableLiveData()
    val speakText: LiveData<String> get() = _speakText

    var phrases: List<Exercise> = listOf()
        set(value) {
            field = value
            phrasesIterator = phrases.listIterator()
        }
    private var phrasesIterator: ListIterator<Exercise> = phrases.listIterator()

    val allExercises: LiveData<List<Exercise>>

    init {
        val exerciseDao = ExercisesDatabase.getDatabase(application, viewModelScope).exerciseDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
        loadNextExercise()
    }

    fun loadNextExercise() {
        Timber.d("loadNextExercise")

        if (phrasesIterator.hasNext()) {
            _currentExercise.value = phrasesIterator.next()
        }

        _exerciseResult.postValue(ExerciseResult.HIDDEN)
    }

    fun loadPreviousExercise() {
        Timber.d("loadPreviousExercise")

        if (phrasesIterator.hasPrevious()) {
            _currentExercise.value = phrasesIterator.previous()
        }

        _exerciseResult.postValue(ExerciseResult.HIDDEN)
    }

    fun validatePhrase(text: String) {
        val result =
            _currentExercise.value?.practicePhrase?.let {
                ExerciseValidator.validatePhrase(
                    text,
                    it
                )
            }
        _exerciseResult.postValue(if (result!!) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)
    }

    fun speakText() {
        _speakText.postValue(_currentExercise.value?.practicePhrase)
    }

    fun deleteCurrentExercise() {
        _currentExercise.value?.let {
            runBlocking {
                repository.delete(it)
            }
        }
    }

}

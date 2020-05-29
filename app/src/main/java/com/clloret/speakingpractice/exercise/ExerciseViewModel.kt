package com.clloret.speakingpractice.exercise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
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

    private var exercises: List<Exercise> = listOf()
    private var exerciseIndex: Int = 0

    init {
        val db = ExercisesDatabase.getDatabase(application, viewModelScope)
        repository = ExerciseRepository(db)

        repository.allExercises.apply {
            observeForever {
                Timber.d("Exercises: $it")

                exercises = it
                showCurrentExercise()
            }
        }
    }

    private fun showCurrentExercise() {
        if (exercises.isEmpty()) {
            return
        }

        Timber.d("Index: $exerciseIndex")

        val exercise = exercises.getOrElse(exerciseIndex) {
            exercises.last()
        }
        showExercise(exercise)
    }

    fun loadNextExercise() {
        Timber.d("loadNextExercise")

        val exercise = exercises.getOrElse(++exerciseIndex) {
            exerciseIndex = exercises.lastIndex
            exercises.last()
        }
        showExercise(exercise)
    }

    fun loadPreviousExercise() {
        Timber.d("loadPreviousExercise")

        val exercise = exercises.getOrElse(--exerciseIndex) {
            exerciseIndex = 0
            exercises.first()
        }
        showExercise(exercise)
    }

    private fun showExercise(exercise: Exercise) {
        _currentExercise.postValue(exercise)
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

        runBlocking {
            ExerciseAttempt(
                exerciseId = currentExercise.value?.id!!,
                result = result!!,
                recognizedText = text
            ).apply {
                repository.insertAttempt(this)
            }
        }

        _exerciseResult.postValue(if (result!!) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)
    }

    fun speakText() {
        _speakText.postValue(_currentExercise.value?.practicePhrase)
    }

    fun deleteCurrentExercise() {
        _currentExercise.value?.let {
            runBlocking {
                repository.deleteExercise(it)
            }
        }
    }

}

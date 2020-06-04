package com.clloret.speakingpractice.exercise.practice

import android.app.Application
import androidx.lifecycle.*
import com.clloret.speakingpractice.App
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.utils.lifecycle.Event
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class PracticeViewModel(application: Application) : AndroidViewModel(application) {
    enum class ExerciseResult {
        HIDDEN, CORRECT, INCORRECT
    }

    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    private var _exerciseResult: MutableLiveData<ExerciseResult> =
        MutableLiveData(ExerciseResult.HIDDEN)
    val exerciseResult: LiveData<ExerciseResult> get() = _exerciseResult

    private val _currentExercise = MutableLiveData<Exercise>()
    val currentExercise: LiveData<Exercise> get() = _currentExercise

    private val currentExerciseId = MutableLiveData<Int>()
    val resultValues = Transformations.switchMap(currentExerciseId) {
        Timber.d("switchMap: exerciseId=$it")
        repository.getResultValues(it)
    }

    private val _speakText: MutableLiveData<Event<String>> = MutableLiveData()
    val speakText: LiveData<Event<String>> get() = _speakText

    private var exercises: List<Exercise> = listOf()
    private var exerciseIndex: Int = 0

    init {
        repository.allExercises.apply {
            observeForever {
                Timber.d("Exercises: $it")

                exercises = it
                showCurrentExercise()
            }
        }
    }

    private fun initRepository(): ExerciseRepository {
        val application = getApplication<App>()
        val db = ExercisesDatabase.getDatabase(application, viewModelScope)
        return ExerciseRepository(db)
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
        Timber.d("showExercise: exercise.id=${exercise.id}")
        Timber.d("showExercise: currentExerciseId.value=${currentExerciseId.value}")

        _currentExercise.postValue(exercise)
        _exerciseResult.postValue(ExerciseResult.HIDDEN)
        if (currentExerciseId.value != exercise.id) {
            Timber.d("showExercise: change")
            currentExerciseId.postValue(exercise.id)
        }
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
        _currentExercise.value?.let {
            _speakText.postValue(Event(it.practicePhrase))
        }
    }

    fun deleteCurrentExercise() {
        _currentExercise.value?.let {
            runBlocking {
                repository.deleteExercise(it)
            }
        }
    }

}

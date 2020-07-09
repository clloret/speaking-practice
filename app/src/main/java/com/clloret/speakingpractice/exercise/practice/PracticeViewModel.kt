package com.clloret.speakingpractice.exercise.practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.lifecycle.Event
import kotlinx.coroutines.runBlocking

class PracticeViewModel(
    filter: ExerciseFilterStrategy,
    private val repository: AppRepository
) :
    ViewModel() {
    enum class ExerciseResult {
        HIDDEN, CORRECT, INCORRECT
    }

    private var _exerciseResult: MutableLiveData<ExerciseResult> =
        MutableLiveData(ExerciseResult.HIDDEN)
    val exerciseResult: LiveData<ExerciseResult> get() = _exerciseResult

    private val _speakText: MutableLiveData<Event<String>> = MutableLiveData()
    val speakText: LiveData<Event<String>> get() = _speakText

    private var currentExerciseDetail: ExerciseWithDetails? = null

    val exercises = filter.getExercises(repository)

    var onClickRecognizeSpeechBtn: (() -> Unit)? = null

    fun recognizeSpeech(exerciseDetail: ExerciseWithDetails) {
        currentExerciseDetail = exerciseDetail
        onClickRecognizeSpeechBtn?.invoke()
    }

    fun speakText(text: String) {
        _speakText.postValue(Event(text))
    }

    fun validatePhrase(text: String) {
        currentExerciseDetail?.let {
            val result = ExerciseValidator.validatePhrase(
                text,
                it.exercise.practicePhrase
            )

            runBlocking {
                ExerciseAttempt(
                    exerciseId = it.exercise.id,
                    result = result,
                    recognizedText = text
                ).apply {
                    repository.insertAttempt(this)
                }

                _exerciseResult.postValue(if (result) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)
            }
        }
    }

    fun resetExercise() {
        _exerciseResult.postValue(ExerciseResult.HIDDEN)
        currentExerciseDetail = null
    }

}

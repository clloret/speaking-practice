package com.clloret.speakingpractice.exercise.practice

import android.app.Application
import android.text.Spannable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.clloret.speakingpractice.App
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.lifecycle.Event
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class PracticeViewModel(
    filter: ExerciseFilterStrategy,
    application: Application,
    private val repository: AppRepository
) :
    AndroidViewModel(application) {
    enum class ExerciseResult {
        HIDDEN, CORRECT, INCORRECT
    }

    private var _exerciseResult: MutableLiveData<ExerciseResult> =
        MutableLiveData(ExerciseResult.HIDDEN)
    val exerciseResult: LiveData<ExerciseResult> get() = _exerciseResult

    private val _speakText: MutableLiveData<Event<String>> = MutableLiveData()
    val speakText: LiveData<Event<String>> get() = _speakText

    private var currentExerciseDetail: ExerciseWithDetails? = null
    private var correctWords = listOf<Pair<String, Boolean>>()

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

            val words = ExerciseValidator.getWordsWithResults(
                text,
                it.exercise.practicePhrase
            )
            correctWords = words
        }
    }

    fun resetExercise() {
        _exerciseResult.postValue(ExerciseResult.HIDDEN)
        currentExerciseDetail = null
        correctWords = listOf()
    }

    private fun isCurrentExercise(exercise: Exercise): Boolean {
        return currentExerciseDetail?.exercise?.let {
            exercise.id == it.id
        } ?: false
    }

    fun getFormattedPracticePhrase(exercise: Exercise): Spannable {
        Timber.d("Correct Words: $correctWords")
        Timber.d("Exercise: $exercise")
        Timber.d("Current Exercise: ${currentExerciseDetail?.exercise}")

        val context = getApplication<App>().applicationContext

        return FormatCorrectWords.getFormattedPracticePhrase(
            context,
            exercise.practicePhrase,
            correctWords,
            isCurrentExercise(exercise)
        )
    }

}

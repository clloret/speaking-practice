package com.clloret.speakingpractice.exercise.practice

import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.entities.PracticeWord
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.lifecycle.Event
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class PracticeViewModel(
    filter: ExerciseFilterStrategy,
    private val repository: AppRepository,
    private val formatCorrectWords: FormatCorrectWords
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
        currentExerciseDetail?.let { it ->
            val result = ExerciseValidator.validatePhrase(
                text,
                it.exercise.practicePhrase
            )

            viewModelScope.launch {
                ExerciseAttempt(
                    exerciseId = it.exercise.id,
                    result = result,
                    recognizedText = text
                ).apply {
                    repository.insertAttempt(this)
                }
            }

            _exerciseResult.postValue(if (result) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)

            val words = ExerciseValidator.getWordsWithResults(
                text,
                it.exercise.practicePhrase
            )
            correctWords = words

            viewModelScope.launch {
                val practiceWords =
                    words.map { word ->
                        PracticeWord(
                            exerciseId = it.exercise.id,
                            word = filterValidChars(word.first),
                            result = word.second
                        )
                    }
                repository.insertAllPracticeWords(practiceWords)
            }
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

    fun getFormattedPracticePhrase(exercise: Exercise): Spanned {
        Timber.d("Correct Words: $correctWords")
        Timber.d("Exercise: $exercise")
        Timber.d("Current Exercise: ${currentExerciseDetail?.exercise}")

        return formatCorrectWords.getFormattedPracticePhrase(
            exercise.practicePhrase,
            correctWords,
            isCurrentExercise(exercise)
        )
    }

    companion object {
        private fun filterValidChars(text: String): String {
            return text
                .filter { it.isLetter() || it == '\'' || it == '’' }
                .toLowerCase(Locale.US)
        }
    }

}

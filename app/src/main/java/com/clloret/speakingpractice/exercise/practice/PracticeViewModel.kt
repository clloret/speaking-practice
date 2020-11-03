package com.clloret.speakingpractice.exercise.practice

import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.db.AttemptRepository
import com.clloret.speakingpractice.domain.ExerciseValidator
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.entities.ExerciseAttempt
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.entities.PracticeWord
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.lifecycle.Event
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class PracticeViewModel(
    filter: ExerciseFilterStrategy,
    repository: AppRepository,
    private val attemptRepository: AttemptRepository,
    private val preferenceValues: PreferenceValues,
    private val formatCorrectWords: FormatCorrectWords,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
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
    private var correctedExercise = false

    val exercises = filter.getExercises(repository)
    var onClickRecognizeSpeechBtn: (() -> Unit)? = null

    private fun isCurrentExercise(exercise: Exercise): Boolean {
        return currentExerciseDetail?.exercise?.let {
            exercise.id == it.id
        } ?: false
    }

    fun setCurrentExercise(exercise: ExerciseWithDetails) {
        Timber.d("setCurrentExercise - current: $currentExerciseDetail, new: $exercise")

        if (currentExerciseDetail?.exercise?.id == exercise.exercise.id) {
            return
        }

        currentExerciseDetail = exercise

        if (preferenceValues.isSpeakPhraseEnabled()) {
            speakText()
        }
    }

    fun resetExercise() {
        _exerciseResult.postValue(ExerciseResult.HIDDEN)
        correctWords = listOf()
        correctedExercise = false
    }

    fun recognizeSpeech() {
        correctedExercise = true
        onClickRecognizeSpeechBtn?.invoke()
    }

    fun speakText() {
        currentExerciseDetail?.let { it ->
            _speakText.postValue(Event(it.practicePhrase))
        }
    }

    fun validatePhrase(textList: List<String>) {
        currentExerciseDetail?.let { it ->
            val result = ExerciseValidator(textList, it.exercise.practicePhrase)
                .getValidPhrase()

            Timber.d("Valid recognized phrase: $result")

            val words = ExerciseValidator.getWordsWithResults(
                result.second,
                it.exercise.practicePhrase
            )
            correctWords = words

            viewModelScope.launch(defaultDispatcher) {
                val exerciseAttempt = ExerciseAttempt(
                    exerciseId = it.exercise.id,
                    result = result.first,
                    recognizedText = result.second
                )
                val practiceWords =
                    words.map { word ->
                        PracticeWord(
                            exerciseAttemptId = it.exercise.id,
                            word = filterValidChars(unifyApostrophes(word.first)),
                            result = word.second
                        )
                    }
                attemptRepository.insertExerciseAttemptAndWords(exerciseAttempt, practiceWords)
            }

            _exerciseResult
                .postValue(if (result.first) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)
        }
    }

    fun getFormattedPracticePhrase(exercise: Exercise): Spanned {
        Timber.d("Correct Words: $correctWords")
        Timber.d("Exercise: $exercise")
        Timber.d("Current Exercise: ${currentExerciseDetail?.exercise}")
        Timber.d("Corrected: $correctedExercise")

        return formatCorrectWords.getFormattedPracticePhrase(
            exercise.practicePhrase,
            correctWords,
            isCurrentExercise(exercise) && correctedExercise
        )
    }

    companion object {
        private fun filterValidChars(text: String): String {
            return text
                .filter { it.isLetter() || it == '’' }
                .toLowerCase(Locale.US)
        }

        private fun unifyApostrophes(text: String): String {
            return text
                .replace('\'', '’', true)
        }
    }

}

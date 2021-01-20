package com.clloret.speakingpractice.exercise.practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.db.repository.StatsRepository
import com.clloret.speakingpractice.domain.*
import com.clloret.speakingpractice.domain.entities.*
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.lifecycle.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.util.*
import kotlin.coroutines.CoroutineContext

class PracticeViewModel(
    filter: ExerciseFilterStrategy,
    exerciseRepository: ExerciseRepository,
    private val statsRepository: StatsRepository,
    private val attemptRepository: AttemptRepository,
    private val preferenceValues: PreferenceValues,
    formatCorrectWords: FormatCorrectWords,
    private val clock: Clock,
    private val delayProvider: DelayProvider,
    private val coroutineContext: CoroutineContext = Dispatchers.Main,
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

    private val _dailyGoalAchieved: MutableLiveData<Event<Unit>> = MutableLiveData()
    val dailyGoalAchieved: LiveData<Event<Unit>> get() = _dailyGoalAchieved

    val exercises = filter.getExercises(exerciseRepository)

    private var currentExerciseDetail: ExerciseWithDetails? = null
    val exerciseStatus = ExerciseStatus(formatCorrectWords)
    private val startTime = Instant.now(clock)

    var onClickRecognizeSpeechBtn: (() -> Unit)? = null

    init {
        viewModelScope.launch(coroutineContext) {
            val dailyStats = DailyStats()
            statsRepository.insertDailyStats(dailyStats)
        }
    }

    override fun onCleared() {
        super.onCleared()

        GlobalScope.launch(coroutineContext) {
            savePracticeTime()
            saveCurrentStrike()
        }
    }

    private suspend fun checkDailyGoal() {
        val dailyGoal = preferenceValues.dailyGoal()
        val today = ISO_LOCAL_DATE.format(LocalDate.now(clock))

        val todayAttempts = attemptRepository.getTotalAttemptsByDay(today)

        Timber.d("Attempts: ${todayAttempts}, Goal: $dailyGoal")
        println("Attempts: ${todayAttempts}, Goal: $dailyGoal")

        if (todayAttempts == dailyGoal) {
            saveDailyGoalAchieved()

            delayProvider.delay(DAILY_GOAL_MSG_DELAY)
            _dailyGoalAchieved.postValue(Event(Unit))
        }
    }

    private suspend fun saveDailyGoalAchieved() {
        val today = LocalDate.now(clock)
        statsRepository.updateDailyGoal(DailyGoalUpd(today))
    }

    private suspend fun insertExerciseAttempt(
        exercise: ExerciseWithDetails,
        result: Pair<Boolean, String>,
        texts: CorrectedWords
    ) {
        val exerciseAttempt = ExerciseAttempt(
            exerciseId = exercise.exercise.id,
            result = result.first,
            recognizedText = result.second
        )
        val practiceWords =
            texts.map { word ->
                PracticeWord(
                    exerciseAttemptId = exercise.exercise.id,
                    word = filterValidChars(unifyApostrophes(word.first)),
                    result = word.second
                )
            }
        attemptRepository.insertExerciseAttemptAndWords(exerciseAttempt, practiceWords)
    }

    suspend fun savePracticeTime() {
        val endTime = Instant.now(clock)
        val lapseInSeconds = Duration.between(startTime, endTime).seconds.toInt()
        val dailyStats = statsRepository.getDailyStatsByDate(LocalDate.now(clock))
        val currentTimePracticing = dailyStats?.timePracticing ?: 0
        val epochDate = LocalDate.now(clock).toEpochDay()
        statsRepository.updateDailyStats(currentTimePracticing + lapseInSeconds, epochDate)
    }

    suspend fun saveCurrentStrike() {
        val currentStats = statsRepository.getStats() ?: Stats(0, LocalDate.MIN, 0, 0)
        val today = LocalDate.now(clock)
        val calcStrike = CalcDailyStreak()
        val streak = calcStrike.calcCurrentStreak(
            today,
            currentStats.lastPracticeDay,
            currentStats.currentStreak
        )
        val longStreak = calcStrike.calcLongStreak(currentStats.longStreak, streak)
        val newStats = Stats(0, today, streak, longStreak)

        Timber.d("Stats: $newStats")

        statsRepository.insertStats(newStats)
    }

    fun setCurrentExercise(exercise: ExerciseWithDetails) {
        Timber.d("setCurrentExercise - current: $currentExerciseDetail, new: $exercise")

        if (currentExerciseDetail?.exercise?.id == exercise.exercise.id) {
            return
        }

        currentExerciseDetail = exercise

        exerciseStatus.resetExercise(exercise.practicePhrase)

        if (preferenceValues.isSpeakPhraseEnabled()) {
            speakText()
        }
    }

    fun resetExercise() {
        _exerciseResult.postValue(ExerciseResult.HIDDEN)
    }

    fun recognizeSpeech() {
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

            val exerciseWords = ExerciseValidator.getWordsWithResults(
                result.second,
                it.exercise.practicePhrase
            )

            val recognizedWords = ExerciseValidator.getWordsWithResults(
                it.exercise.practicePhrase,
                result.second
            )

            exerciseStatus.saveCorrectedExercise(exerciseWords, recognizedWords, result.second)

            viewModelScope.launch(coroutineContext) {
                insertExerciseAttempt(it, result, exerciseWords)
                checkDailyGoal()
            }

            _exerciseResult
                .postValue(if (result.first) ExerciseResult.CORRECT else ExerciseResult.INCORRECT)
        }
    }

    companion object {
        const val DAILY_GOAL_MSG_DELAY = 2000L

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

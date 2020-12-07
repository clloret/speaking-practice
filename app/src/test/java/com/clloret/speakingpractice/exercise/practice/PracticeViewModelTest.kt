package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.db.repository.StatsRepository
import com.clloret.speakingpractice.domain.entities.DailyStats
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterAll
import com.clloret.speakingpractice.util.MainCoroutineScopeRule
import com.clloret.speakingpractice.util.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors


@ExperimentalCoroutinesApi
@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class PracticeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineScopeRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val colorResourceProvider = TestColorResourceProvider()
    private val preferenceValues = FakePreferenceValues()
    private val formatCorrectWords = FormatCorrectWords(colorResourceProvider)

    private val clock = FakeClock(ofEpochMilli(0))
    private lateinit var db: AppDatabase
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var attemptRepository: AttemptRepository
    private lateinit var statsRepository: StatsRepository
    private lateinit var sut: PracticeViewModel

    @Before
    fun createDb() {
        val context = getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .allowMainThreadQueries().build()
        val exercise = Exercise(practicePhrase = CORRECT_PHRASE, translatedPhrase = "Hola Mundo!!")

        mainCoroutineRule.launch {
            db.exerciseDao().insert(exercise)
        }

        exerciseRepository = ExerciseRepository(db)
        attemptRepository = AttemptRepository(db)
        statsRepository = StatsRepository(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Before
    fun createSut() {
        val filter = ExerciseFilterAll()
        sut = PracticeViewModel(
            filter,
            exerciseRepository,
            statsRepository,
            attemptRepository,
            preferenceValues,
            formatCorrectWords,
            clock,
            testDispatcher
        )
    }

    @Test
    fun `when start set exercise result as hidden`() {
        TestObserver.test(sut.exerciseResult)
            .awaitValue()
            .assertHasValue()
            .assertValue { it == PracticeViewModel.ExerciseResult.HIDDEN }
    }

    @Test
    fun `when the validated phrase is correct set exercise result as correct`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(CORRECT_PHRASE))

        println(first.exercise.id)

        TestObserver.test(sut.exerciseResult)
            .awaitValue()
            .assertHasValue()
            .assertValue { it == PracticeViewModel.ExerciseResult.CORRECT }
    }

    @Test
    fun `when the validated phrase is incorrect set exercise result as incorrect`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val firstExercise = exercises.first()

        sut.setCurrentExercise(firstExercise)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(INCORRECT_PHRASE))

        TestObserver.test(sut.exerciseResult)
            .awaitValue()
            .assertHasValue()
            .assertValue { it == PracticeViewModel.ExerciseResult.INCORRECT }
    }

    @Test
    fun `when validate phrase always insert an exercise attempt`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(CORRECT_PHRASE))

        val attempts =
            db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(first.exercise.id)

        TestObserver.test(attempts)
            .awaitValue()
            .assertHasValue()
            .assertValue { it.isNotEmpty() }
    }

    @Test
    fun `when call speakTest return the text to speak with TTS`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.speakText()

        TestObserver.test(sut.speakText)
            .awaitValue()
            .assertHasValue()
            .assertValue { it.peekContent() == CORRECT_PHRASE }
    }

    @Test
    fun `when current streak is 1 and date is 1 days later streak equal 2`() = runBlocking {

        sut.saveCurrentStrike()

        var stats = db.statsDao().getStats()

        assertThat(stats?.currentStreak).isEqualTo(1)
        assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))

        clock.addDays(1)

        sut.saveCurrentStrike()

        stats = db.statsDao().getStats()

        assertThat(stats?.currentStreak).isEqualTo(2)
        assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))
    }

    @Test
    fun `when current streak is 1 and date is 2 days later streak equal 1`() = runBlocking {

        sut.saveCurrentStrike()

        var stats = db.statsDao().getStats()

        assertThat(stats?.currentStreak).isEqualTo(1)
        assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))

        clock.addDays(2)

        sut.saveCurrentStrike()

        stats = db.statsDao().getStats()

        assertThat(stats?.currentStreak).isEqualTo(1)
        assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))
    }

    @Test
    fun `when current streak is 1 and date is same day streak equal 1`() = runBlocking {

        sut.saveCurrentStrike()

        var stats = db.statsDao().getStats()

        assertThat(stats?.currentStreak).isEqualTo(1)
        assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))

        sut.saveCurrentStrike()

        stats = db.statsDao().getStats()

        assertThat(stats?.currentStreak).isEqualTo(1)
        assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))
    }

    @Test
    fun `when save practice time save correct time`() = runBlocking {

        val today = LocalDate.now(clock)

        statsRepository.insertDailyStats(DailyStats(today))

        var dailyStats = db.statsDao().getDailyStatsByDate(today)

        assertThat(dailyStats?.timePracticing).isEqualTo(0)

        clock.addSeconds(60)

        sut.savePracticeTime()

        dailyStats = db.statsDao().getDailyStatsByDate(today)

        assertThat(dailyStats?.timePracticing).isEqualTo(60)

        sut.savePracticeTime()

        dailyStats = db.statsDao().getDailyStatsByDate(today)

        assertThat(dailyStats?.timePracticing).isEqualTo(120)
    }

    companion object {
        const val CORRECT_PHRASE = "Hello World!!"
        const val INCORRECT_PHRASE = "Goodbye World!!"
    }

}

class FakeClock(private var instant: Instant) : Clock() {
    override fun getZone(): ZoneId {
        return ZoneId.systemDefault()
    }

    override fun withZone(zone: ZoneId?): Clock {
        return this
    }

    override fun instant(): Instant {
        return instant
    }

    fun addDays(amount: Long) {
        instant = instant.plus(amount, ChronoUnit.DAYS)
    }

    fun addSeconds(amount: Long) {
        instant = instant.plus(amount, ChronoUnit.SECONDS)
    }
}

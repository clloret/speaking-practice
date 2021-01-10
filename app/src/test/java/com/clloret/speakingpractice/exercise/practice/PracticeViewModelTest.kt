package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.repository.AttemptRepository
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.db.repository.StatsRepository
import com.clloret.speakingpractice.domain.entities.DailyStats
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterAll
import com.clloret.speakingpractice.fakes.FakeClock
import com.clloret.speakingpractice.fakes.FakeColorResourceProvider
import com.clloret.speakingpractice.fakes.FakeDelayProvider
import com.clloret.speakingpractice.fakes.FakePreferenceValues
import com.clloret.speakingpractice.util.getOrAwaitValue
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.util.concurrent.Executors
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class PracticeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    private val colorResourceProvider = FakeColorResourceProvider()
    private val preferenceValues = FakePreferenceValues()
    private val formatCorrectWords = FormatCorrectWords(colorResourceProvider)
    private val delayProvider = FakeDelayProvider()

    private val clock = FakeClock(Instant.now())
    private lateinit var db: AppDatabase
    private lateinit var exerciseRepository: ExerciseRepository
    private lateinit var attemptRepository: AttemptRepository
    private lateinit var statsRepository: StatsRepository
    private lateinit var sut: PracticeViewModel

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .allowMainThreadQueries()
            .build()

        val exercise =
            Exercise(practicePhrase = CORRECT_PHRASE, translatedPhrase = "Hola Mundo!!")

        testScope.launch {
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
            delayProvider,
            testDispatcher
        )
    }

    @Test
    fun `when call speakTest return the text to speak with TTS`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.speakText()

        Truth.assertThat(sut.speakText.getOrAwaitValue().peekContent())
            .isEqualTo(CORRECT_PHRASE)
    }

    @Test
    fun `when current streak is 1 and date is 2 days later streak equal 1`() = runBlocking {

        sut.saveCurrentStrike()

        var stats = db.statsDao().getStats()

        Truth.assertThat(stats?.currentStreak).isEqualTo(1)
        Truth.assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))

        clock.addDays(2)

        sut.saveCurrentStrike()

        stats = db.statsDao().getStats()

        Truth.assertThat(stats?.currentStreak).isEqualTo(1)
        Truth.assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))
    }

    @Test
    fun `when start set exercise result as hidden`() {
        Truth.assertThat(sut.exerciseResult.getOrAwaitValue())
            .isEqualTo(PracticeViewModel.ExerciseResult.HIDDEN)
    }

    @Test
    fun `when save practice time save correct time`() = runBlocking {

        val today = LocalDate.now(clock)

        statsRepository.insertDailyStats(DailyStats(today))

        var dailyStats = db.statsDao().getDailyStatsByDate(today)

        Truth.assertThat(dailyStats?.timePracticing).isEqualTo(0)

        clock.addSeconds(60)

        sut.savePracticeTime()

        dailyStats = db.statsDao().getDailyStatsByDate(today)

        Truth.assertThat(dailyStats?.timePracticing).isEqualTo(60)

        sut.savePracticeTime()

        dailyStats = db.statsDao().getDailyStatsByDate(today)

        Truth.assertThat(dailyStats?.timePracticing).isEqualTo(120)
    }

    @Test
    fun `when current streak is 1 and date is same day streak equal 1`() = runBlocking {

        sut.saveCurrentStrike()

        var stats = db.statsDao().getStats()

        Truth.assertThat(stats?.currentStreak).isEqualTo(1)
        Truth.assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))

        sut.saveCurrentStrike()

        stats = db.statsDao().getStats()

        Truth.assertThat(stats?.currentStreak).isEqualTo(1)
        Truth.assertThat(stats?.lastPracticeDay).isEqualTo(LocalDate.now(clock))
    }

    @Test
    fun `when validate phrase always insert an exercise attempt`() = testScope.runBlockingTest {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(CORRECT_PHRASE))

        val count =
            db.exerciseAttemptDao().getExercisesAttemptsCount()

        Truth.assertThat(count.getOrAwaitValue())
            .isEqualTo(1)
    }

    @Test
    fun `when the validated phrase is correct set exercise result as correct`() =
        testScope.runBlockingTest {
            val exercises = sut.exercises.getOrAwaitValue()
            val first = exercises.first()

            sut.setCurrentExercise(first)
            sut.recognizeSpeech()
            sut.validatePhrase(arrayListOf(CORRECT_PHRASE))

            Truth.assertThat(sut.exerciseResult.getOrAwaitValue())
                .isEqualTo(PracticeViewModel.ExerciseResult.CORRECT)
        }

    @Test
    fun `when the validated phrase is incorrect set exercise result as incorrect`() =
        testScope.runBlockingTest {
            val exercises = sut.exercises.getOrAwaitValue()
            val firstExercise = exercises.first()

            sut.setCurrentExercise(firstExercise)
            sut.recognizeSpeech()
            sut.validatePhrase(arrayListOf(INCORRECT_PHRASE))

            Truth.assertThat(sut.exerciseResult.getOrAwaitValue())
                .isEqualTo(PracticeViewModel.ExerciseResult.INCORRECT)
        }

    @Test
    fun `when the daily goal is achieved notify to the view`() = testScope.runBlockingTest {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(CORRECT_PHRASE))
        sut.validatePhrase(arrayListOf(INCORRECT_PHRASE))

        Truth.assertThat(sut.dailyGoalAchieved.getOrAwaitValue())
            .isNotNull()
    }

    @Test(expected = TimeoutException::class)
    fun `when the daily goal is not achieved do nothing`() = testScope.runBlockingTest {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(CORRECT_PHRASE))

        Truth.assertThat(sut.dailyGoalAchieved.getOrAwaitValue())
            .isNull()
    }

    @Test
    fun `when the daily goal is achieved save in daily stats`() = testScope.runBlockingTest {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.setCurrentExercise(first)
        sut.recognizeSpeech()
        sut.validatePhrase(arrayListOf(CORRECT_PHRASE))
        sut.validatePhrase(arrayListOf(INCORRECT_PHRASE))

        Truth.assertThat(sut.dailyGoalAchieved.getOrAwaitValue())
            .isNotNull()

        // Without the runBlocking the test fail with exception "This job has not completed yet"
        // More info in https://github.com/Kotlin/kotlinx.coroutines/issues/1204
        @Suppress("BlockingMethodInNonBlockingContext")
        val dailyStats = runBlocking(testDispatcher) {
            val today = LocalDate.now(clock)
            db.statsDao().getDailyStatsByDate(today)
        }
        println(dailyStats)
        Truth.assertThat(dailyStats?.dailyGoalAchieved).isTrue()
    }

    companion object {
        const val CORRECT_PHRASE = "Hello World!!"
        const val INCORRECT_PHRASE = "Goodbye World!!"
    }

}

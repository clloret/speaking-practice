package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterAll
import com.clloret.speakingpractice.util.MainCoroutineScopeRule
import com.clloret.speakingpractice.util.getOrAwaitValue
import com.jraska.livedata.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
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
    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var sut: PracticeViewModel

    @Before
    fun createDb() {
        val context = getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        )
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .allowMainThreadQueries().build()
        val exercise = Exercise(practicePhrase = CORRECT_PHRASE, translatedPhrase = "Hola Mundo!!")

        mainCoroutineRule.launch {
            db.exerciseDao().insert(exercise)
        }

        repository = AppRepository(db)
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
            repository,
            preferenceValues,
            formatCorrectWords,
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

    companion object {
        const val CORRECT_PHRASE = "Hello World!!"
        const val INCORRECT_PHRASE = "Goodbye World!!"
    }

}

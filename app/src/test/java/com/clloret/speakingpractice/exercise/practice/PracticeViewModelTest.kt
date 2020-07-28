package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterAll
import com.clloret.speakingpractice.util.MainCoroutineScopeRule
import com.clloret.speakingpractice.util.getOrAwaitValue
import com.jraska.livedata.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class PracticeViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineScopeRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val colorResourceProvider = TestColorResourceProvider()
    private val formatCorrectWords = FormatCorrectWords(colorResourceProvider)
    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository
    private lateinit var sut: PracticeViewModel

    @Before
    fun createDb() {
        val context = getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
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
        sut = PracticeViewModel(filter, repository, formatCorrectWords)
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

        sut.recognizeSpeech(first)
        sut.validatePhrase(CORRECT_PHRASE)

        println(first.exercise.id)

        val attempts =
            db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(first.exercise.id)

        TestObserver.test(sut.exerciseResult)
            .awaitValue()
            .assertHasValue()
            .assertValue { it == PracticeViewModel.ExerciseResult.CORRECT }

        TestObserver.test<List<AttemptWithExercise>>(attempts)
            .awaitValue()
            .assertHasValue()
            .assertValue { it.isNotEmpty() }
    }

    @Test
    fun `when the validated phrase is correct set exercise result as incorrect`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val firstExercise = exercises.first()

        sut.recognizeSpeech(firstExercise)
        sut.validatePhrase(INCORRECT_PHRASE)

        TestObserver.test(sut.exerciseResult)
            .awaitValue()
            .assertHasValue()
            .assertValue { it == PracticeViewModel.ExerciseResult.INCORRECT }
    }

    @Test
    fun `when validate phrase always insert an exercise attempt`() {
        val exercises = sut.exercises.getOrAwaitValue()
        val first = exercises.first()

        sut.recognizeSpeech(first)
        sut.validatePhrase(CORRECT_PHRASE)

        val attempts =
            db.exerciseAttemptDao().getExerciseAttemptsByExerciseId(first.exercise.id)

        TestObserver.test<List<AttemptWithExercise>>(attempts)
            .awaitValue()
            .assertHasValue()
            .assertValue { it.isNotEmpty() }
    }

    @Test
    fun `when call speakTest return the text to speak with TTS`() {
        sut.speakText(CORRECT_PHRASE)

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
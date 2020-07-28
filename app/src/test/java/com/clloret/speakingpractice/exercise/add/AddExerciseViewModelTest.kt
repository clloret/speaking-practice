package com.clloret.speakingpractice.exercise.add

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.Exercise
import com.clloret.speakingpractice.exercise.add.AddExerciseViewModel.FormErrors
import com.clloret.speakingpractice.util.MainCoroutineScopeRule
import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AddExerciseViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineScopeRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries()
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .build()

        val exercise = Exercise(
            practicePhrase = "Hello World!!",
            translatedPhrase = "Hola Mundo!!"
        )

        runBlocking {
            db.exerciseDao().insert(exercise)
        }

        repository = AppRepository(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `when save exercise with empty data don't save and set form errors`() {
        val sut = AddExerciseViewModel(repository, -1)

        sut.saveExercise()

        assertThat(sut.formErrors)
            .containsExactlyElementsIn(
                arrayOf(
                    FormErrors.EMPTY_PRACTICE_PHRASE,
                    FormErrors.EMPTY_TRANSLATED_PHRASE
                )
            )

        TestObserver.test(sut.getSaveData())
            .awaitValue()
            .assertHasValue()
            .assertValue { !it }
    }

    @Test
    fun `when save new exercise store the exercise in the database`() {
        val sut = AddExerciseViewModel(repository, -1)

        val testExercise = Exercise(
            2,
            "New exercise",
            "Nuevo ejercicio"
        ).apply {
            sut.practicePhrase.set(practicePhrase)
            sut.translatedPhrase.set(translatedPhrase)
            sut.exerciseTags.set(emptyList())
        }

        sut.saveExercise()

        TestObserver.test(sut.getSaveData())
            .awaitValue(2, TimeUnit.SECONDS)
            .assertHasValue()
            .assertValue { it }

        val exercise = runBlocking { db.exerciseDao().getExerciseById(testExercise.id) }

        assertThat(exercise)
            .isEqualTo(testExercise)
    }
}
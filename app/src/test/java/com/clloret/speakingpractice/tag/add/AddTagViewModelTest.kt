package com.clloret.speakingpractice.tag.add

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.tag.add.AddTagViewModel.FormErrors
import com.clloret.speakingpractice.util.MainCoroutineScopeRule
import com.google.common.truth.Truth
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
class AddTagViewModelTest {
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

        val tag = Tag(
            name = "Sample tag"
        )

        runBlocking {
            db.tagDao().insert(tag)
        }

        repository = AppRepository(db)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun `when save tag with empty data don't save and set form errors`() {
        val sut = AddTagViewModel(repository, -1)

        sut.saveData()

        Truth.assertThat(sut.formErrors)
            .containsExactlyElementsIn(
                arrayOf(
                    FormErrors.EMPTY_NAME
                )
            )

        TestObserver.test(sut.getSaveData())
            .awaitValue()
            .assertHasValue()
            .assertValue { !it }
    }

    @Test
    fun `when save new tag store the exercise in the database`() {
        val sut = AddTagViewModel(repository, -1)

        val testTag = Tag(
            2,
            "New tag"
        ).apply {
            sut.name.set(name)
        }

        sut.saveData()

        TestObserver.test(sut.getSaveData())
            .awaitValue(2, TimeUnit.SECONDS)
            .assertHasValue()
            .assertValue { it }

        val tag = runBlocking { db.tagDao().getTagById(testTag.id) }

        Truth.assertThat(tag)
            .isEqualTo(testTag)
    }

}
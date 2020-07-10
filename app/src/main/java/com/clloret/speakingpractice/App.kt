package com.clloret.speakingpractice

import android.app.Application
import android.content.Context
import com.clloret.speakingpractice.attempt.list.AttemptListViewModel
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.exercise.filter.*
import com.clloret.speakingpractice.exercise.add.AddExerciseViewModel
import com.clloret.speakingpractice.exercise.import_.ImportExercises
import com.clloret.speakingpractice.exercise.list.ExerciseListViewModel
import com.clloret.speakingpractice.exercise.practice.PracticeViewModel
import com.clloret.speakingpractice.exercise.practice.filter.SelectTagDlgViewModel
import com.clloret.speakingpractice.tag.add.AddTagViewModel
import com.clloret.speakingpractice.tag.list.TagListViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        setupLog()
        setupKoin()
    }

    private fun setupKoin() {
        val appModule = module {

            single { AppDatabase.getDatabase(get(), get()) }
            single { AppRepository(get()) }
            single { (context: Context) -> ImportExercises(context) }
            single { ExerciseFilterAll() }
            single { ExerciseFilterBySuccessRate() }
            single { (limit: Int) ->
                ExerciseFilterByRandom(limit)
            }
            single { (limit: Int) ->
                ExerciseFilterByLessPracticed(limit)
            }
            single { (tagId: Int) ->
                ExerciseFilterByTag(tagId)
            }

            viewModel { (filter: ExerciseFilterStrategy) ->
                PracticeViewModel(filter, get())
            }
            viewModel { ExerciseListViewModel(get()) }
            viewModel { TagListViewModel(get()) }
            viewModel { SelectTagDlgViewModel(get()) }
            viewModel { (exerciseId: Int) ->
                AttemptListViewModel(get(), exerciseId)
            }
            viewModel { (exerciseId: Int) ->
                AddExerciseViewModel(get(), exerciseId)
            }
            viewModel { (tagId: Int) ->
                AddTagViewModel(get(), tagId)
            }

            factory { SupervisorJob() }
            factory { CoroutineScope(get<CompletableJob>()) }
        }

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@App)

            // module list
            modules(appModule)
        }
    }

    private fun setupLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

package com.clloret.speakingpractice

import android.app.Application
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.exercise.list.ExerciseListViewModel
import com.clloret.speakingpractice.exercise.practice.PracticeViewModel
import com.clloret.speakingpractice.exercise.practice.filter.SelectTagDlgViewModel
import com.clloret.speakingpractice.tag.list.TagListViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
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

            single { ExercisesDatabase.getDatabase(get(), get()) }
            single { ExerciseRepository(get()) }

            viewModel { (filter: ExerciseFilterStrategy) ->
                PracticeViewModel(
                    get(),
                    filter,
                    get()
                )
            }
            viewModel { ExerciseListViewModel(get(), get()) }
            viewModel { TagListViewModel(get(), get()) }
            viewModel { SelectTagDlgViewModel(get()) }

            factory { SupervisorJob() }
            factory { CoroutineScope(get<CompletableJob>()) }
        }

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger()

            // use the Android context given there
            androidContext(this@App)

            // load properties from assets/koin.properties file
            androidFileProperties()

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

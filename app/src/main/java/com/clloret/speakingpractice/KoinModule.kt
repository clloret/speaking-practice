package com.clloret.speakingpractice

import android.content.Context
import com.clloret.speakingpractice.attempt.list.AttemptListViewModel
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.db.repository.*
import com.clloret.speakingpractice.domain.DelayProvider
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterStrategy
import com.clloret.speakingpractice.domain.exercise.list.sort.*
import com.clloret.speakingpractice.domain.exercise.practice.filter.*
import com.clloret.speakingpractice.domain.resources.ColorResourceProvider
import com.clloret.speakingpractice.domain.resources.StringResourceProvider
import com.clloret.speakingpractice.domain.word.sort.*
import com.clloret.speakingpractice.exercise.add.AddExerciseViewModel
import com.clloret.speakingpractice.exercise.file.export.ExportExercises
import com.clloret.speakingpractice.exercise.file.import_.ImportExercises
import com.clloret.speakingpractice.exercise.list.ExerciseListViewModel
import com.clloret.speakingpractice.exercise.practice.FormatCorrectWords
import com.clloret.speakingpractice.exercise.practice.PracticeViewModel
import com.clloret.speakingpractice.exercise.practice.filter.PracticeFilterViewModel
import com.clloret.speakingpractice.exercise.practice.filter.SelectTagDlgViewModel
import com.clloret.speakingpractice.home.stats.HomeStatsViewModel
import com.clloret.speakingpractice.stats.StatsViewModel
import com.clloret.speakingpractice.tag.add.AddTagViewModel
import com.clloret.speakingpractice.tag.list.TagListViewModel
import com.clloret.speakingpractice.utils.DelayProviderImpl
import com.clloret.speakingpractice.utils.PreferenceValuesImpl
import com.clloret.speakingpractice.utils.resources.ColorResourceProviderImpl
import com.clloret.speakingpractice.utils.resources.StringResourceProviderImpl
import com.clloret.speakingpractice.word.WordListViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.Clock

object KoinModule {

    fun setupKoin(context: Context) {
        val appModule = module {

            data(this)
            utils(this)
            exerciseFilters(this)
            practiceWordSorters(this)
            exerciseListSorters(this)
            viewModels(this)
        }

        startKoin {
            // use AndroidLogger as Koin Logger - default Level.INFO
            androidLogger(Level.ERROR)

            // use the Android context given there
            androidContext(context)

            // module list
            modules(appModule)
        }
    }

    private fun data(module: Module) {
        // Data

        module.apply {
            single { AppDatabase.getDatabase(get(), get()) }
            single { ExerciseRepository(get()) }
            single { StatsRepository(get()) }
            single { TagRepository(get()) }
            single { AttemptRepository(get()) }
            single { WordRepository(get()) }
        }
    }

    private fun utils(module: Module) {
        // Utils

        module.apply {

            single<ColorResourceProvider> {
                ColorResourceProviderImpl(get())
            }
            single<StringResourceProvider> {
                StringResourceProviderImpl(get())
            }
            single<PreferenceValues> { PreferenceValuesImpl(get(), get()) }
            single { FormatCorrectWords(get()) }
            single { (context: Context) -> ImportExercises(context) }
            single { (context: Context) -> ExportExercises(context) }
            single { Clock.systemDefaultZone() }
            single<DelayProvider> { DelayProviderImpl() }
        }
    }

    private fun exerciseFilters(module: Module) {
        // Exercise Filters

        module.apply {

            single { ExerciseFilterAll() }
            single { ExerciseFilterBySuccessRate() }
            factory { (limit: Int) ->
                ExerciseFilterByRandom(limit)
            }
            factory { (limit: Int) ->
                ExerciseFilterByLessPracticed(limit)
            }

        }
    }

    private fun practiceWordSorters(module: Module) {
        // Practice Word Sorters

        module.apply {

            single<Comparator<WordSortable>>(named("WordSortByTextAsc")) {
                WordSortByText(
                    WordSortStrategy.OrderType.ASC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortByTextDesc")) {
                WordSortByText(
                    WordSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortByCorrectDesc")) {
                WordSortByCorrect(
                    WordSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortByIncorrectDesc")) {
                WordSortByIncorrect(
                    WordSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortBySuccessRateAsc")) {
                WordSortBySuccessRate(
                    WordSortStrategy.OrderType.ASC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortBySuccessRateDesc")) {
                WordSortBySuccessRate(
                    WordSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortByPracticedAsc")) {
                WordSortByPracticed(
                    WordSortStrategy.OrderType.ASC
                )
            }
            single<Comparator<WordSortable>>(named("WordSortByPracticedDesc")) {
                WordSortByPracticed(
                    WordSortStrategy.OrderType.DESC
                )
            }

        }
    }

    private fun viewModels(module: Module) {
        // View Models

        module.apply {

            viewModel { (filter: ExerciseFilterStrategy) ->
                PracticeViewModel(filter, get(), get(), get(), get(), get(), get(), get())
            }
            viewModel { ExerciseListViewModel(get(), get()) }
            viewModel { TagListViewModel(get()) }
            viewModel { SelectTagDlgViewModel(get()) }
            viewModel { StatsViewModel(get(), get()) }
            viewModel { HomeStatsViewModel(get(), get()) }
            viewModel { WordListViewModel(get()) }
            viewModel { PracticeFilterViewModel(get()) }
            viewModel { (filter: AttemptFilterStrategy) ->
                AttemptListViewModel(filter, get(), get())
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

    }

    private fun exerciseListSorters(module: Module) {
        // Exercise List Sorters

        module.apply {
            single<Comparator<ExerciseSortable>>(named("ExerciseSortByTextAsc")) {
                ExerciseSortByText(
                    ExerciseSortStrategy.OrderType.ASC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortByTextDesc")) {
                ExerciseSortByText(
                    ExerciseSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortByCorrectDesc")) {
                ExerciseSortByCorrect(
                    ExerciseSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortByIncorrectDesc")) {
                ExerciseSortByIncorrect(
                    ExerciseSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortBySuccessRateAsc")) {
                ExerciseSortBySuccessRate(
                    ExerciseSortStrategy.OrderType.ASC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortBySuccessRateDesc")) {
                ExerciseSortBySuccessRate(
                    ExerciseSortStrategy.OrderType.DESC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortByPracticedAsc")) {
                ExerciseSortByPracticed(
                    ExerciseSortStrategy.OrderType.ASC
                )
            }
            single<Comparator<ExerciseSortable>>(named("ExerciseSortByPracticedDesc")) {
                ExerciseSortByPracticed(
                    ExerciseSortStrategy.OrderType.DESC
                )
            }
        }

    }
}

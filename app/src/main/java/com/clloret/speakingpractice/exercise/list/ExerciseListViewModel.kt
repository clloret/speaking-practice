package com.clloret.speakingpractice.exercise.list

import androidx.lifecycle.*
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.exercise.sort.ExerciseSortable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ExerciseListViewModel(private val repository: AppRepository) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_TIMEOUT = 300L
    }

    private val compositeDisposable = CompositeDisposable()
    private var unfilteredData = emptyList<ExerciseWithDetails>()

    private val _filtered: MutableLiveData<List<ExerciseWithDetails>> = MutableLiveData()
    val filtered: LiveData<List<ExerciseWithDetails>> get() = _filtered
    val exercises = MediatorLiveData<List<ExerciseWithDetails>>()

    var selectedComparator: Comparator<ExerciseSortable>? = null
    var sortItemId: Int? = null

    init {
        exercises.addSource(repository.allExercisesDetails) {
            unfilteredData = it
            exercises.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.dispose()
    }

    private fun filterByText(text: String) {
        val list = unfilteredData
            .filter { it.practicePhrase.contains(text, ignoreCase = true) }
        _filtered.postValue(list)
    }

    private fun removeFilter() {
        _filtered.postValue(unfilteredData)
    }

    fun deleteExerciseList(list: List<Int>) {
        viewModelScope.launch {
            repository.deleteExerciseList(list)
        }
    }

    fun observeSearchQuery(observable: Observable<String>) {
        val subscribe = observable
            .debounce(
                SEARCH_DEBOUNCE_TIMEOUT,
                TimeUnit.MILLISECONDS
            )
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { filter: String? ->
                Timber.d("Filter exercises: %s", filter)
                if (filter.isNullOrBlank()) {
                    removeFilter()
                } else {
                    filterByText(filter)
                }
            }
        compositeDisposable.add(subscribe)
    }

}

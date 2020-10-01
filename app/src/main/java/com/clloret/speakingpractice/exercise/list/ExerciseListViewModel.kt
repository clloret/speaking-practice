package com.clloret.speakingpractice.exercise.list

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.domain.exercise.sort.ExerciseSortable
import com.clloret.speakingpractice.utils.databinding.ChipChoiceBinding
import com.clloret.speakingpractice.utils.databinding.adapters.TagChipChoice
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

    private val _filteredExercises: MutableLiveData<List<ExerciseWithDetails>> = MutableLiveData()
    val filteredExercises: LiveData<List<ExerciseWithDetails>> get() = _filteredExercises
    val exercises = MediatorLiveData<List<ExerciseWithDetails>>()
    var fieldTags: ObservableField<List<ChipChoiceBinding>> = ObservableField()
    private val allTagsObserver = Observer<List<Tag>> { value ->
        Timber.d("$value")
        fieldTags.set(value.map { TagChipChoice(it) })
    }
    private val fieldTagsCallback =
        object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: androidx.databinding.Observable, i: Int) {
                testTags()
            }
        }

    var selectedComparator: Comparator<ExerciseSortable>? = null
    var sortItemId: Int? = null
    var filterQuery: String? = null

    init {
        exercises.addSource(repository.allExercisesDetails) {
            unfilteredData = it
            exercises.postValue(it)
        }

        fieldTags.addOnPropertyChangedCallback(fieldTagsCallback)

        repository.allTags.observeForever(allTagsObserver)
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.dispose()
        fieldTags.removeOnPropertyChangedCallback(fieldTagsCallback)
        repository.allTags.removeObserver(allTagsObserver)
    }

    private fun filterByText(text: String) {
        val list = unfilteredData
            .filter { it.practicePhrase.contains(text, ignoreCase = true) }
        _filteredExercises.postValue(list)
        filterQuery = text
    }

    private fun filterByTags(tagIds: List<Int>) {
        val list = unfilteredData
            .filter { exercise -> exercise.tags.map { it.id }.any { it in tagIds } }
        _filteredExercises.postValue(list)
    }

    private fun removeFilter() {
        _filteredExercises.postValue(unfilteredData)
        filterQuery = null
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

    private fun getSelectedTagsIds(selectedTags: List<ChipChoiceBinding>): List<Int> {
        val selectedTagIds = arrayListOf<Int>()
        selectedTags.forEach {
            Timber.d("Tag: $it")
            if (it.selected) {
                selectedTagIds.add(it.id)
            }
        }
        return selectedTagIds
    }

    fun testTags() {
        val selectedTagIds = getSelectedTagsIds(fieldTags.get() ?: return)
        Timber.d("Selected tags: $selectedTagIds")

        if (selectedTagIds.isNotEmpty()) {
            filterByTags(selectedTagIds)
        } else {
            removeFilter()
        }
    }

}

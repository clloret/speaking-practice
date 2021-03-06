package com.clloret.speakingpractice.exercise.list

import androidx.databinding.ObservableField
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clloret.speakingpractice.db.repository.ExerciseRepository
import com.clloret.speakingpractice.db.repository.TagRepository
import com.clloret.speakingpractice.domain.common.criteria.FilterChain
import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails
import com.clloret.speakingpractice.domain.entities.Tag
import com.clloret.speakingpractice.domain.exercise.list.filter.ExerciseCriteriaByTag
import com.clloret.speakingpractice.domain.exercise.list.filter.ExerciseCriteriaByText
import com.clloret.speakingpractice.domain.exercise.list.sort.ExerciseSortable
import com.clloret.speakingpractice.utils.databinding.ChipChoiceBinding
import com.clloret.speakingpractice.utils.databinding.adapters.TagChipChoice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ExerciseListViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_TIMEOUT = 300L
    }

    private val compositeDisposable = CompositeDisposable()
    private var unfilteredData = emptyList<ExerciseWithDetails>()
    private val allTagsObserver = Observer<List<Tag>> { value ->
        fieldTags.set(value.map { TagChipChoice(it) }.sortedBy { it.displayName })
    }
    private val fieldTagsCallback =
        object : androidx.databinding.Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: androidx.databinding.Observable, i: Int) {
                updateTagFilter()
            }
        }
    private val filterChain = FilterChain<ExerciseWithDetails>()
    val exercises = MediatorLiveData<List<ExerciseWithDetails>>()
    val fieldTags: ObservableField<List<ChipChoiceBinding>> = ObservableField()
    var selectedComparator: Comparator<ExerciseSortable>? = null
    var sortItemId: Int? = null
    var filterQuery: String? = null

    init {
        exercises.addSource(exerciseRepository.allExercisesDetails) {
            unfilteredData = it

            val meetCriteria = filterChain.meetCriteria(unfilteredData)
            exercises.postValue(meetCriteria)
        }

        fieldTags.addOnPropertyChangedCallback(fieldTagsCallback)

        tagRepository.allTags.observeForever(allTagsObserver)
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.dispose()
        fieldTags.removeOnPropertyChangedCallback(fieldTagsCallback)
        tagRepository.allTags.removeObserver(allTagsObserver)
    }

    private fun filterByText(text: String) {
        filterChain.addFilter(ExerciseCriteriaByText.KEY, ExerciseCriteriaByText(text))
        showFilteredData()

        filterQuery = text
    }

    private fun filterByTags(tagIds: List<Int>) {
        filterChain.addFilter(ExerciseCriteriaByTag.KEY, ExerciseCriteriaByTag(tagIds))
        showFilteredData()
    }

    private fun removeTextFilter() {
        filterChain.removeFilter(ExerciseCriteriaByText.KEY)
        showFilteredData()

        filterQuery = null
    }

    private fun removeTagsFilter() {
        filterChain.removeFilter(ExerciseCriteriaByTag.KEY)
        showFilteredData()
    }

    fun deleteExerciseList(list: List<Int>) {
        viewModelScope.launch {
            exerciseRepository.deleteExerciseList(list)
        }
    }

    private fun showFilteredData() {
        val meetCriteria = filterChain.meetCriteria(unfilteredData)
        exercises.postValue(meetCriteria)
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
                    removeTextFilter()
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

    private fun updateTagFilter() {
        val selectedTagIds = getSelectedTagsIds(fieldTags.get() ?: return)
        Timber.d("Selected tags: $selectedTagIds")

        if (selectedTagIds.isNotEmpty()) {
            filterByTags(selectedTagIds)
        } else {
            removeTagsFilter()
        }
    }

}

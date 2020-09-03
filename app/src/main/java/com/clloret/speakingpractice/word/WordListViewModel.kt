package com.clloret.speakingpractice.word

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clloret.speakingpractice.db.AppRepository
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults
import com.clloret.speakingpractice.domain.word.sort.WordSortable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class WordListViewModel(repository: AppRepository) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_TIMEOUT = 300L
    }

    private val compositeDisposable = CompositeDisposable()
    private var unfilteredData = emptyList<PracticeWordWithResults>()

    private val _filteredWords: MutableLiveData<List<PracticeWordWithResults>> =
        MutableLiveData()
    val filteredWords: LiveData<List<PracticeWordWithResults>> get() = _filteredWords
    val words = MediatorLiveData<List<PracticeWordWithResults>>()

    var selectedComparator: Comparator<WordSortable>? = null
    var sortItemId: Int? = null
    var filterQuery: String? = null

    init {
        words.addSource(repository.allPracticeWords) {
            unfilteredData = it
            words.postValue(it)
        }
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.dispose()
    }

    private fun filterByText(text: String) {
        val list = unfilteredData
            .filter { it.word.contains(text, ignoreCase = true) }
        _filteredWords.postValue(list)
        filterQuery = text
    }

    private fun removeFilter() {
        _filteredWords.postValue(unfilteredData)
        filterQuery = null
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
                Timber.d("Filter words: %s", filter)
                if (filter.isNullOrBlank()) {
                    removeFilter()
                } else {
                    filterByText(filter)
                }
            }
        compositeDisposable.add(subscribe)
    }

}
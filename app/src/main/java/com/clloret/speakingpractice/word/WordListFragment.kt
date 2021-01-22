package com.clloret.speakingpractice.word

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListFragmentBinding
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterByWord
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterByWordIncorrect
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterByWord
import com.clloret.speakingpractice.domain.word.sort.WordSortable
import com.clloret.speakingpractice.exercise.list.RxSearchObservable
import com.clloret.speakingpractice.utils.RecyclerViewEmptyObserver
import com.clloret.speakingpractice.utils.ScrollToTopButton
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import java.util.*

class WordListFragment : BaseFragment(), WordListAdapter.WordListListener {

    companion object {
        fun newInstance() = WordListFragment()
    }

    private val viewModel: WordListViewModel by viewModel()
    private val sortByAlphaAsc: Comparator<WordSortable> by inject(named("WordSortByTextAsc"))
    private val sortByAlphaDesc: Comparator<WordSortable> by inject(named("WordSortByTextDesc"))
    private val sortByCorrect: Comparator<WordSortable> by inject(named("WordSortByCorrectDesc"))
    private val sortByIncorrect: Comparator<WordSortable> by inject(named("WordSortByIncorrectDesc"))
    private val sortBySuccessRateAsc: Comparator<WordSortable> by inject(named("WordSortBySuccessRateAsc"))
    private val sortBySuccessRateDesc: Comparator<WordSortable> by inject(named("WordSortBySuccessRateDesc"))
    private val sortByPracticedAsc: Comparator<WordSortable> by inject(named("WordSortByPracticedAsc"))
    private val sortByPracticedDesc: Comparator<WordSortable> by inject(named("WordSortByPracticedDesc"))

    private var _ui: WordListFragmentBinding? = null
    private val ui get() = _ui!!
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = DataBindingUtil.inflate(
            inflater,
            R.layout.word_list_fragment, container, false
        )

        ui.lifecycleOwner = viewLifecycleOwner
        ui.recyclerView.setupRecyclerView(ui.emptyView)

        ScrollToTopButton.configure(ui.scrollToTopButton, ui.recyclerView)

        return ui.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    private fun selectStoredSortOrder(menu: Menu) {
        val itemId = viewModel.sortItemId ?: R.id.menu_word_sort_alphabetically_asc
        menu.findItem(itemId).isChecked = true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_word_list, menu)
        configureSearchView(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        selectStoredSortOrder(menu)

        viewModel.filterQuery?.let { query ->
            searchView?.isIconified = false
            searchView?.setQuery(query, false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_word_sort_alphabetically_asc -> selectSortMenuItem(item, sortByAlphaAsc)
            R.id.menu_word_sort_alphabetically_desc -> selectSortMenuItem(item, sortByAlphaDesc)
            R.id.menu_word_sort_correct -> selectSortMenuItem(item, sortByCorrect)
            R.id.menu_word_sort_incorrect -> selectSortMenuItem(item, sortByIncorrect)
            R.id.menu_word_sort_success_rate_asc -> selectSortMenuItem(item, sortBySuccessRateAsc)
            R.id.menu_word_sort_success_rate_desc -> selectSortMenuItem(item, sortBySuccessRateDesc)
            R.id.menu_word_sort_more_practiced -> selectSortMenuItem(item, sortByPracticedDesc)
            R.id.menu_word_sort_less_practiced -> selectSortMenuItem(item, sortByPracticedAsc)
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun selectSortMenuItem(item: MenuItem, comparator: Comparator<WordSortable>): Boolean {
        sortBy(comparator)
        item.isChecked = true
        viewModel.selectedComparator = comparator
        viewModel.sortItemId = item.itemId
        return true
    }

    private fun sortBy(comparator: Comparator<WordSortable>) {
        val wordListAdapter = ui.recyclerView.adapter as WordListAdapter
        wordListAdapter.setOrder(comparator)
    }

    private fun configureSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.menu_word_search)
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        val activity: FragmentActivity = requireActivity()
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
            viewModel.observeSearchQuery(RxSearchObservable.fromView(this))
        }
    }

    private fun RecyclerView.setupRecyclerView(emptyView: View) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            context,
            linearLayoutManager.orientation
        )
        addItemDecoration(dividerItemDecoration)

        val comparator = viewModel.selectedComparator ?: sortByAlphaAsc
        val listAdapter = WordListAdapter(comparator, this@WordListFragment)
        adapter = listAdapter

        val rvEmptyObserver = RecyclerViewEmptyObserver(this, emptyView)
        listAdapter.registerAdapterDataObserver(rvEmptyObserver)

        viewModel.words.observe(viewLifecycleOwner, {
            it?.let {
                listAdapter.submitList(it)
            }
        })

        viewModel.filteredWords.observe(viewLifecycleOwner, {
            listAdapter.submitList(it)
        })

    }

    override fun onShowExerciseAttempts(word: String) {
        val action = WordListFragmentDirections.actionWordListFragmentToAttemptListFragment(
            AttemptFilterByWord(word)
        )
        findNavController().navigate(action)
    }

    override fun onShowIncorrectExerciseAttempts(word: String) {
        val action = WordListFragmentDirections.actionWordListFragmentToAttemptListFragment(
            AttemptFilterByWordIncorrect(word)
        )
        findNavController().navigate(action)
    }

    override fun onPracticeWordExercises(word: String) {
        val action = WordListFragmentDirections.actionWordListFragmentToPracticeActivity(
            ExerciseFilterByWord(word), "Practice “${word.capitalize(Locale.US)}”"
        )
        findNavController().navigate(action)
    }

}

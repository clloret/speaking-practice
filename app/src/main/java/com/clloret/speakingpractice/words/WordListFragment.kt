package com.clloret.speakingpractice.words

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListFragmentBinding
import com.clloret.speakingpractice.domain.word.sort.WordSortable
import com.clloret.speakingpractice.utils.RecyclerViewEmptyObserver
import kotlinx.android.synthetic.main.word_list_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class WordListFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: WordListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.word_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.setupRecyclerView(binding.emptyView)

        return binding.root
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
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        selectStoredSortOrder(menu)
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
        val wordListAdapter = recyclerView.adapter as WordListAdapter
        wordListAdapter.setOrder(comparator)
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
        val listAdapter = WordListAdapter(comparator, findNavController())
        adapter = listAdapter

        val rvEmptyObserver = RecyclerViewEmptyObserver(this, emptyView)
        listAdapter.registerAdapterDataObserver(rvEmptyObserver)

        viewModel.words.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.submitList(it)
            }
        })
    }

}
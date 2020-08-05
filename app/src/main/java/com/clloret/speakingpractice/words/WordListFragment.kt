package com.clloret.speakingpractice.words

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WordListFragment : Fragment() {

    companion object {
        fun newInstance() = WordListFragment()
    }

    private val viewModel: WordListViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: WordListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.word_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.setupRecyclerView()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_word_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.menu_word_sort_correct -> sortByCorrect()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sortByCorrect(): Boolean {
        TODO("Not yet implemented")
    }

    private fun RecyclerView.setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            context,
            linearLayoutManager.orientation
        )
        addItemDecoration(dividerItemDecoration)

        val listAdapter = WordListAdapter()
        adapter = listAdapter

        viewModel.words.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.submitList(it)
            }
        })
    }

}
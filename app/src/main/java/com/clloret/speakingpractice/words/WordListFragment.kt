package com.clloret.speakingpractice.words

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
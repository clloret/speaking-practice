package com.clloret.speakingpractice.attempt.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AttemptListFragmentBinding
import com.clloret.speakingpractice.db.ExerciseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.android.ext.android.inject

class AttemptListFragment : Fragment(), CoroutineScope by MainScope() {

    companion object {
        fun newInstance() = AttemptListFragment()
    }

    private val repository: ExerciseRepository by inject()

    private val viewModel: AttemptListViewModel by viewModels {
        AttemptListViewModelFactory(repository, args.exerciseId)
    }

    private val args: AttemptListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: AttemptListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.attempt_list_fragment, container, false
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

        val listAdapter = AttemptListAdapter()
        adapter = listAdapter

        viewModel.attempts.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.submitList(it)
            }
        })
    }
}

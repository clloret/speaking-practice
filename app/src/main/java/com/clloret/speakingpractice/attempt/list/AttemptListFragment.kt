package com.clloret.speakingpractice.attempt.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AttemptListFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AttemptListFragment : Fragment(), CoroutineScope by MainScope() {

    companion object {
        fun newInstance() = AttemptListFragment()
    }

    private val viewModel: AttemptListViewModel by viewModel { parametersOf(args.exerciseId) }
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

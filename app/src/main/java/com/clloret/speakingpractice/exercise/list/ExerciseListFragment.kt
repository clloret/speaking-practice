package com.clloret.speakingpractice.exercise.list

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListFragmentBinding

class ExerciseListFragment : Fragment() {

    companion object {
        fun newInstance() = ExerciseListFragment()
    }

    private val viewModel: ExerciseListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ExerciseListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.exercise_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ExerciseListAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.exercises.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.action_exercise_list).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }
}

package com.clloret.speakingpractice.exercise.practice.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterAll
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterByTag
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import kotlinx.android.synthetic.main.practice_filter_fragment.*
import timber.log.Timber

class PracticeFilterFragment : Fragment() {

    companion object {
        fun newInstance() = PracticeFilterFragment()
    }

    private val sharedViewModel: SharedViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.practice_filter_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonsEvents()
        observeData()
    }

    private fun observeData() {
        sharedViewModel.selected.observe(
            viewLifecycleOwner,
            EventObserver {
                it.let {
                    Timber.d("Tag: $it")

                    val filter = ExerciseFilterByTag(it.id)
                    val action =
                        PracticeFilterFragmentDirections.actionPracticeFilterFragmentToPracticeFragment(
                            filter
                        )

                    findNavController().apply {
                        popBackStack()
                        navigate(action)
                    }
                }
            })
    }

    private fun setupButtonsEvents() {
        btnAllExercises.setOnClickListener {
            val filter = ExerciseFilterAll()
            val action =
                PracticeFilterFragmentDirections.actionPracticeFilterFragmentToPracticeFragment(
                    filter
                )

            findNavController()
                .navigate(action)
        }

        btnOneTag.setOnClickListener {
            val action =
                PracticeFilterFragmentDirections.actionPracticeFilterFragmentToSelectTagDlgFragment()

            findNavController()
                .navigate(action)
        }
    }

}
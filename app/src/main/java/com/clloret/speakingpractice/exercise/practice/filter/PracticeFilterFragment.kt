package com.clloret.speakingpractice.exercise.practice.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.exercise.filter.*
import com.clloret.speakingpractice.utils.PreferenceValues
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import kotlinx.android.synthetic.main.practice_filter_fragment.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class PracticeFilterFragment : BaseFragment() {

    companion object {
        fun newInstance() = PracticeFilterFragment()
    }

    private val sharedViewModel: SharedViewModel by navGraphViewModels(R.id.nav_graph)
    private val preferenceValues: PreferenceValues by inject()
    private val filterAll: ExerciseFilterAll by inject()
    private val filterBySuccessRate: ExerciseFilterBySuccessRate by inject()

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

                    findNavController().popBackStack()

                    val filter = ExerciseFilterByTag(it.id)
                    showPracticeWithFilter(filter, it.name)

                }
            })
    }

    private fun setupButtonsEvents() {
        btnAllExercises.setOnClickListener {
            showPracticeWithFilter(filterAll, "All")
        }

        btnRandomExercises.setOnClickListener {
            val filterByRandom: ExerciseFilterByRandom =
                get {
                    parametersOf(
                        preferenceValues.exercisesPerRound()
                    )
                }
            showPracticeWithFilter(filterByRandom, "Random")
        }

        btnLessPracticedExercises.setOnClickListener {
            val filterByLessPracticed: ExerciseFilterByLessPracticed =
                get {
                    parametersOf(
                        preferenceValues.exercisesPerRound()
                    )
                }
            showPracticeWithFilter(filterByLessPracticed, "Less Practiced")
        }

        btnMostFailedExercises.setOnClickListener {
            showPracticeWithFilter(filterBySuccessRate, "Most Failed")
        }

        btnOneTag.setOnClickListener {
            val action =
                PracticeFilterFragmentDirections.actionPracticeFilterFragmentToSelectTagDlgFragment()

            findNavController()
                .navigate(action)
        }
    }

    private fun showPracticeWithFilter(filter: ExerciseFilterStrategy, filterName: String) {
        val action =
            PracticeFilterFragmentDirections.actionPracticeFilterFragmentToPracticeActivity(
                filter,
                "Practice $filterName"
            )

        findNavController()
            .navigate(action)
    }

}
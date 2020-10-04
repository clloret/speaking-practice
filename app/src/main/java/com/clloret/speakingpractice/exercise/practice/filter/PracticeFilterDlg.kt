package com.clloret.speakingpractice.exercise.practice.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.exercise.practice.filter.*
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.practice_filter_dlg.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class PracticeFilterDlg : BottomSheetDialogFragment() {

    private val viewModel: PracticeFilterViewModel by viewModel()
    private val sharedViewModel: SharedViewModel by navGraphViewModels(R.id.nav_graph)
    private val preferenceValues: PreferenceValues by inject()
    private val filterAll: ExerciseFilterAll by inject()
    private val filterBySuccessRate: ExerciseFilterBySuccessRate by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.practice_filter_dlg, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupButtonsEvents()
        observeData()
    }

    private fun observeData() {
        viewModel.exerciseAttemptsCount.observe(viewLifecycleOwner) {
            val enable = it > 0
            btnLessPracticedExercises.isEnabled = enable
            btnMostFailedExercises.isEnabled = enable
        }

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
            showPracticeWithFilter(filterAll, getString(R.string.title_exercise_filter_all))
        }

        btnRandomExercises.setOnClickListener {
            val filterByRandom: ExerciseFilterByRandom =
                get {
                    parametersOf(
                        preferenceValues.exercisesPerRound()
                    )
                }
            showPracticeWithFilter(filterByRandom, getString(R.string.title_exercise_filter_random))
        }

        btnLessPracticedExercises.setOnClickListener {
            val filterByLessPracticed: ExerciseFilterByLessPracticed =
                get {
                    parametersOf(
                        preferenceValues.exercisesPerRound()
                    )
                }
            showPracticeWithFilter(
                filterByLessPracticed,
                getString(R.string.title_exercise_filter_less_practiced)
            )
        }

        btnMostFailedExercises.setOnClickListener {
            showPracticeWithFilter(
                filterBySuccessRate,
                getString(R.string.title_exercise_filter_most_failed)
            )
        }

        btnOneTag.setOnClickListener {
            val action =
                PracticeFilterDlgDirections.actionPracticeFilterDlgToSelectTagDlgFragment()

            findNavController()
                .navigate(action)
        }
    }

    private fun showPracticeWithFilter(filter: ExerciseFilterStrategy, filterName: String) {
        val action =
            PracticeFilterDlgDirections.actionPracticeFilterDlgToPracticeActivity(
                filter,
                "Practice $filterName"
            )

        findNavController()
            .navigate(action)
    }

}
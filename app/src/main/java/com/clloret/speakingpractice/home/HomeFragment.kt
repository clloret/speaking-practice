package com.clloret.speakingpractice.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterByRandom
import com.clloret.speakingpractice.exercise.import_.ImportExercises
import com.clloret.speakingpractice.exercise.import_.ImportExercisesSharedViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class HomeFragment : BaseFragment() {

    companion object {
        private const val HELP_URL =
            "https://github.com/clloret/speaking-practice/wiki"
        private const val CHANGELOG_URL =
            "https://github.com/clloret/speaking-practice/blob/master/CHANGELOG.md"
        private const val IMPORT_HELP_URL =
            "https://github.com/clloret/speaking-practice/wiki/Import-exercises"
    }

    private val sharedViewModel: ImportExercisesSharedViewModel by navGraphViewModels(R.id.nav_graph)
    private val importExercises: ImportExercises by inject { parametersOf(this.requireContext()) }
    private val preferenceValues: PreferenceValues by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel.onShowHelp = {
            findNavController().popBackStack()

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(IMPORT_HELP_URL)
                )
            )
        }

        sharedViewModel.onSelectFile = {
            findNavController().popBackStack()

            this.importExercises()
        }

        importExercises.apply {
            onCompletion = { count ->
                showSnackBar("$count exercises imported successfully")
            }
        }

        setupButtonsEvents()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home_help -> showHelp()
            R.id.menu_home_changelog -> showChangelog()
            else -> NavigationUI.onNavDestinationSelected(
                item,
                requireView().findNavController()
            ) || super.onOptionsItemSelected(item)
        }
    }

    private fun setupButtonsEvents() {
        btnPractice.setOnClickListener {
            val filterByRandom: ExerciseFilterByRandom =
                get {
                    parametersOf(
                        preferenceValues.exercisesPerRound()
                    )
                }
            val action =
                HomeFragmentDirections.actionHomeFragmentToPracticeActivity(
                    filterByRandom,
                    getString(R.string.title_exercise_filter_random)
                )

            findNavController()
                .navigate(action)
        }

        btnPracticeFilter.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToPracticeFilterFragment()

            findNavController()
                .navigate(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        importExercises.onActivityResult(requestCode, resultCode, data, lifecycleScope)
    }

    private fun importExercises() {
        importExercises.performFileSearchFromFragment(this)
    }

    private fun showHelp(): Boolean {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(HELP_URL)
            )
        )
        return true
    }

    private fun showChangelog(): Boolean {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(CHANGELOG_URL)
            )
        )
        return true
    }

}

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
import com.clloret.speakingpractice.databinding.HomeFragmentBinding
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterByRandom
import com.clloret.speakingpractice.exercise.file.common.ImportExportSharedViewModel
import com.clloret.speakingpractice.exercise.file.export.ExportExercises
import com.clloret.speakingpractice.exercise.file.import_.ImportExercises
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
        private const val EXPORT_HELP_URL =
            "https://github.com/clloret/speaking-practice/wiki/Export-exercises"
    }

    private val sharedViewModel: ImportExportSharedViewModel by navGraphViewModels(R.id.nav_graph)
    private val importExercises: ImportExercises by inject { parametersOf(this.requireContext()) }
    private val exportExercises: ExportExercises by inject { parametersOf(this.requireContext()) }
    private val preferenceValues: PreferenceValues by inject()
    private var _ui: HomeFragmentBinding? = null
    private val ui get() = _ui!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = HomeFragmentBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupImportActions()
        setupExportActions()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        importExercises.onActivityResult(requestCode, resultCode, data, lifecycleScope)
        exportExercises.onActivityResult(requestCode, resultCode, data, lifecycleScope)
    }

    private fun setupExportActions() {
        sharedViewModel.onShowExportHelp = {
            findNavController().popBackStack()

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(EXPORT_HELP_URL)
                )
            )
        }

        sharedViewModel.onSelectFileToSave = {
            findNavController().popBackStack()

            this.exportExercises()
        }

        exportExercises.apply {
            onCompletion = { count ->
                val message = getString(R.string.msg_exercises_exported_successfully, count)
                showSnackBar(message)
            }
        }
    }

    private fun setupImportActions() {
        sharedViewModel.onShowImportHelp = {
            findNavController().popBackStack()

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(IMPORT_HELP_URL)
                )
            )
        }

        sharedViewModel.onSelectFileToOpen = {
            findNavController().popBackStack()

            this.importExercises()
        }

        importExercises.apply {
            onCompletion = { count ->
                val message = getString(R.string.msg_exercises_imported_successfully, count)
                showSnackBar(message)
            }
        }
    }

    private fun setupButtonsEvents() {
        ui.btnPractice.setOnClickListener {
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

        ui.btnPracticeFilter.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToPracticeFilterDlg()

            findNavController()
                .navigate(action)
        }
    }

    private fun importExercises() {
        importExercises.performFileSearchFromFragment(this)
    }

    private fun exportExercises() {
        exportExercises.performFileSaveFromFragment(this)
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

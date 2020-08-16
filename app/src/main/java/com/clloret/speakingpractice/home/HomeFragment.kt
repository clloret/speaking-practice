package com.clloret.speakingpractice.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.exercise.import_.ImportExercises
import com.clloret.speakingpractice.exercise.import_.ImportExercisesSharedViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class HomeFragment : Fragment() {

    companion object {
        private const val HELP_URL =
            "https://github.com/clloret/speaking-practice/wiki/Import-exercises"
    }

    private val sharedViewModel: ImportExercisesSharedViewModel by navGraphViewModels(R.id.nav_graph)
    private val importExercises: ImportExercises by inject { parametersOf(this.requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedViewModel.onShowHelp = {
            findNavController().popBackStack()

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(HELP_URL)
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
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        ) || super.onOptionsItemSelected(item)
    }

    private fun setupButtonsEvents() {
        btnPractice.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToPracticeFilterFragment()

            findNavController()
                .navigate(action)
        }

        btnExerciseList.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToExerciseListFragment()

            findNavController()
                .navigate(action)
        }

        btnWordList.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToWordListFragment()

            findNavController()
                .navigate(action)
        }

        btnTagList.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToTagListFragment()

            findNavController()
                .navigate(action)
        }

        btnImportExercises.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToImportExercisesDlgFragment()

            findNavController()
                .navigate(action)
        }

        btnStatistics.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToStatisticsFragment()

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

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

}

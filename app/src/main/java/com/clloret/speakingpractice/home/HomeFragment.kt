package com.clloret.speakingpractice.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.exercise.import_.ImportExercises
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var importExercises: ImportExercises? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        importExercises = ImportExercises(requireContext()).apply {
            onCompletion = { count ->
                showSnackBar("$count exercises imported successfully")
            }
        }

        setupButtonsEvents()
    }

    private fun setupButtonsEvents() {
        btnPractice.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToExerciseFragment()

            findNavController()
                .navigate(action)
        }

        btnExerciseList.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToExerciseListFragment()

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
            this.importExercises()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        importExercises?.onActivityResult(requestCode, resultCode, data)
    }

    private fun importExercises() {
        importExercises?.performFileSearchFromFragment(this)
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

}

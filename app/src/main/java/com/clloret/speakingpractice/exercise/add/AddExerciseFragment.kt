package com.clloret.speakingpractice.exercise.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AddExerciseFragmentBinding
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class AddExerciseFragment : Fragment(), CoroutineScope by MainScope() {

    companion object {
        fun newInstance() = AddExerciseFragment()
    }

    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    private val viewModel: AddExerciseViewModel by viewModels {
        AddExerciseViewModelFactory(repository, args.exerciseId)
    }

    private val args: AddExerciseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: AddExerciseFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.add_exercise_fragment, container, false
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeData()
    }

    private fun initRepository(): ExerciseRepository {
        val db = ExercisesDatabase.getDatabase(requireContext(), this)
        return ExerciseRepository(
            db
        )
    }

    private fun observeData() {
        viewModel.getSaveData().observe(viewLifecycleOwner, Observer { saved ->
            saved?.let {
                if (saved) {
                    findNavController().navigateUp()
                } else {
                    showSnackBar("Phrases can't be empty")
                }
            }
        })
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

}

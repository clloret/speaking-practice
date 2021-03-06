package com.clloret.speakingpractice.exercise.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AddExerciseFragmentBinding
import com.clloret.speakingpractice.utils.KeyboardUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddExerciseFragment : BaseFragment() {

    companion object {
        fun newInstance() = AddExerciseFragment()
    }

    private val viewModel: AddExerciseViewModel by viewModel { parametersOf(args.exerciseId) }
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

    override fun onDestroy() {
        KeyboardUtils.hideSoftKeyboard(requireActivity())

        super.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeData()
    }

    private fun observeData() {
        viewModel.getSaveData().observe(viewLifecycleOwner, { saved ->
            if (saved) {
                findNavController().navigateUp()
            }
        })
    }

}

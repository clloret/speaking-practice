package com.clloret.speakingpractice.tag.add

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
import com.clloret.speakingpractice.databinding.AddTagFragmentBinding
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class AddTagFragment : Fragment(), CoroutineScope by MainScope() {

    companion object {
        fun newInstance() = AddTagFragment()
    }

    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    private val viewModel: AddTagViewModel by viewModels {
        AddTagViewModelFactory(repository, args.tagId)
    }

    private val args: AddTagFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: AddTagFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.add_tag_fragment, container, false
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

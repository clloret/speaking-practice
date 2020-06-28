package com.clloret.speakingpractice.tag.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AddTagFragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AddTagFragment : Fragment(), CoroutineScope by MainScope() {

    companion object {
        fun newInstance() = AddTagFragment()
    }

    private val viewModel: AddTagViewModel by viewModel { parametersOf(args.tagId) }
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

    private fun observeData() {
        viewModel.getSaveData().observe(viewLifecycleOwner, Observer { saved ->
            if (saved) {
                findNavController().navigateUp()
            }
        })
    }
}

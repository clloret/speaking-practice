package com.clloret.speakingpractice.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.StatsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatsFragment : BaseFragment() {

    companion object {
        fun newInstance() = StatsFragment()
    }

    private val viewModel: StatsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: StatsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.stats_fragment, container, false
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}
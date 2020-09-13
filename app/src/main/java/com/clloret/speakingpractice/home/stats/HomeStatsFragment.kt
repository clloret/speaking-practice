package com.clloret.speakingpractice.home.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.HomeStatsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeStatsFragment : Fragment() {

    companion object {
        fun newInstance() = HomeStatsFragment()
    }

    private val viewModel: HomeStatsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: HomeStatsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_stats_fragment, container, false
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}
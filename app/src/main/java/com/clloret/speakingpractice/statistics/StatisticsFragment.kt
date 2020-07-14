package com.clloret.speakingpractice.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.StatisticsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticsFragment : Fragment() {

    companion object {
        fun newInstance() = StatisticsFragment()
    }

    //private lateinit var viewModel: StatisticsViewModel
    private val viewModel: StatisticsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.statistics_fragment, container, false)
        val binding: StatisticsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.statistics_fragment, container, false
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

}
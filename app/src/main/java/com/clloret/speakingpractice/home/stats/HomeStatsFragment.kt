package com.clloret.speakingpractice.home.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.HomeStatsFragmentBinding
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterByDate
import com.clloret.speakingpractice.home.HomeFragmentDirections
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

        viewModel.onClickDayStats = {
            showAttempts(it)
        }

        return binding.root
    }

    private fun showAttempts(date: String) {

        val action =
            HomeFragmentDirections.actionHomeFragmentToAttemptListFragment(
                AttemptFilterByDate(date),
                R.string.title_empty_attempt_list_day
            )

        findNavController().navigate(action)
    }

}

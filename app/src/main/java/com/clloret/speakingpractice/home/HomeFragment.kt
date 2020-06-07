package com.clloret.speakingpractice.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clloret.speakingpractice.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

    }

}

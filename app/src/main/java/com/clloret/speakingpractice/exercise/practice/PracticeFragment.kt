package com.clloret.speakingpractice.exercise.practice

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeFragmentBinding
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.exercise.practice.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.controls.CustomToast
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class PracticeFragment : BaseFragment() {

    companion object {
        fun newInstance(filter: ExerciseFilterStrategy): PracticeFragment {
            val fragment = PracticeFragment()
            Bundle().apply {
                putSerializable(EXTRA_FILTER, filter)
                fragment.arguments = this
            }

            return fragment
        }

        private const val EXTRA_FILTER = "filter"
        private const val PERMISSION_REQUEST_RECORD_AUDIO = 0x01
        private const val MOVE_NEXT_EXERCISE_DELAY_IN_MILLIS = 2000L
    }

    private var viewModel: PracticeViewModel? = null
    private val preferenceValues: PreferenceValues by inject()
    private val tts by lazy {
        PracticeTextToSpeech(requireContext().applicationContext) {
            showSnackBar(it)
        }
    }
    private val speechRecognizer by lazy {
        PracticeSpeechRecognizer(requireContext().applicationContext, onResults = {
            viewModel?.validatePhrase(it)
        }, showMessage = {
            showSnackBar(it)
        })
    }
    private val playSound by lazy {
        PracticePlaySound(requireContext().applicationContext, preferenceValues)
    }
    private val toastExerciseCorrect by lazy {
        CustomToast.makeText(
            requireActivity().applicationContext,
            R.string.title_exercise_correct,
            R.drawable.correct_shape,
            R.drawable.ic_exercise_correct_wht_24dp,
            Toast.LENGTH_SHORT
        )
    }
    private val toastExerciseIncorrect by lazy {
        CustomToast.makeText(
            requireActivity().applicationContext,
            R.string.title_exercise_incorrect,
            R.drawable.incorrect_shape,
            R.drawable.ic_exercise_incorrect_wht_24dp,
            Toast.LENGTH_SHORT
        )
    }
    private val toastDailyGoalAchieved by lazy {
        CustomToast.makeText(
            requireActivity().applicationContext,
            R.string.title_daily_goal_achieved,
            R.drawable.correct_shape,
            R.drawable.ic_daily_goal_achieved_wht_24dp,
            Toast.LENGTH_SHORT
        )
    }
    private var currentPage: Int? = null
    private var viewPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val filter = arguments?.getSerializable(EXTRA_FILTER) ?: return
        viewModel = getViewModel { parametersOf(filter) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: PracticeFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.practice_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewModel
        setupViewPager(binding.viewPager, binding.tabLayout)
        speechRecognizer.setupViews(binding.recognitionProgressView, binding.btnSpeakPhrase)

        observeData()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        playSound.initSoundPool()
        tts.initTts()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        tts.stopTts()
        playSound.stopSoundPool()
        speechRecognizer.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun setupViewPager(viewPager: ViewPager2, tabLayout: TabLayout) {
        val viewModel = viewModel ?: return
        val listAdapter = PracticeAdapter(viewModel)
        viewPager.adapter = listAdapter

        this.viewPager = viewPager

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            Timber.d("TabLayoutMediator - tag: $tab, position: $position")
        }.attach()

        viewPager.setPageTransformer(DepthTransformation())

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Timber.d("onPageSelected - $position")

                if (position == currentPage) {
                    // When swipe left onPageSelected sometimes return the same position
                    return
                }

                currentPage = position

                val item = listAdapter.getItem(position)
                viewModel.setCurrentExercise(item)

                val lastPage = listAdapter.itemCount - 1
                if (position < lastPage) {
                    viewModel.resetExercise()
                }
            }
        })

        viewModel.exercises.observe(viewLifecycleOwner, {
            it?.let {
                listAdapter.submitList(it)
            }
        })
    }

    private fun observeData() {
        val viewModel = viewModel ?: return

        viewModel.speakText.observe(
            viewLifecycleOwner,
            EventObserver {
                Timber.d("speakText: $it")
                tts.textToSpeech(it)
            })

        viewModel.dailyGoalAchieved.observe(
            viewLifecycleOwner,
            EventObserver {
                dailyGoalAchieved()
            })

        viewModel.exerciseResult.observe(viewLifecycleOwner, {

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (it) {
                PracticeViewModel.ExerciseResult.CORRECT -> exerciseCorrect()
                PracticeViewModel.ExerciseResult.INCORRECT -> exerciseIncorrect()
            }
        })

        viewModel.onClickRecognizeSpeechBtn = {
            startRecognizeSpeech()
        }
    }

    private fun exerciseCorrect() {
        toastExerciseCorrect.show()
        playSound.playCorrect()
        moveToNextExercise()
    }

    private fun exerciseIncorrect() {
        toastExerciseIncorrect.show()
        playSound.playIncorrect()
    }

    private fun dailyGoalAchieved() {
        toastDailyGoalAchieved.show()
        playSound.playDailyGoalAchieved()
    }

    private fun moveToNextExercise() {
        Handler().postDelayed({
            viewPager?.let {
                val itemCount = it.adapter?.itemCount ?: 0
                val nextItem = it.currentItem + 1
                if (nextItem < itemCount) {
                    it.setCurrentItem(nextItem, true)
                }
            }
        }, MOVE_NEXT_EXERCISE_DELAY_IN_MILLIS)
    }


    @AfterPermissionGranted(PERMISSION_REQUEST_RECORD_AUDIO)
    private fun startRecognizeSpeech() {
        val perms = Manifest.permission.RECORD_AUDIO
        if (EasyPermissions.hasPermissions(requireContext(), perms)) {
            speechRecognizer.startListening()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.msg_rationale_permission_record_audio),
                PERMISSION_REQUEST_RECORD_AUDIO, perms
            )
        }
    }

}

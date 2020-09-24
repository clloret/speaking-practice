package com.clloret.speakingpractice.exercise.practice

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeFragmentBinding
import com.clloret.speakingpractice.domain.PreferenceValues
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.controls.CustomToast
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.practice_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.*

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
        private const val RECOGNIZER_PROGRESS_ROTATION_RADIUS = 20
        private const val RECOGNIZER_PROGRESS_HIDE_DELAY = 2000L
    }

    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(
            requireContext()
        )
    }
    private var viewModel: PracticeViewModel? = null
    private val preferenceValues: PreferenceValues by inject()
    private val tts by lazy {
        PracticeTextToSpeech(requireContext().applicationContext) {
            Timber.d("ShowMessage: $it")
            showSnackBar(it)
        }
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

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        val binding: PracticeFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.practice_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = viewModel
        setupViewPager(binding.viewPager, binding.tabLayout)
        setupRecognizerProgressView(binding.recognitionProgressView)

        observeData()

        return binding.root
    }

    private fun setupViewPager(viewPager: ViewPager2, tabLayout: TabLayout) {
        val viewModel = viewModel ?: return
        val listAdapter = PracticeAdapter(viewModel)
        viewPager.adapter = listAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            Timber.d("TabLayoutMediator - tag: $tab, position: $position")
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

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

    private fun setupRecognizerProgressView(progressView: RecognitionProgressView) {
        with(progressView) {
            setSingleColor(ContextCompat.getColor(requireContext(), R.color.color_secondary))
            setRotationRadiusInDp(RECOGNIZER_PROGRESS_ROTATION_RADIUS)
            setSpeechRecognizer(speechRecognizer)
            setRecognitionListener(object : RecognitionListenerAdapter() {
                override fun onResults(results: Bundle) {
                    processRecognizedText(results)
                }

                override fun onEndOfSpeech() {
                    super.onEndOfSpeech()

                    Timber.d("onEndOfSpeech")

                    apply {
                        postDelayed({
                            stop()
                            play()
                            visibility = View.GONE
                            btnSpeakPhrase.visibility = View.VISIBLE
                        }, RECOGNIZER_PROGRESS_HIDE_DELAY)
                    }
                }

                override fun onError(error: Int) {
                    super.onError(error)

                    Timber.d("onError - error:$error")

                    visibility = View.GONE
                    btnSpeakPhrase.visibility = View.VISIBLE
                }
            })
            visibility = View.GONE
            play()
        }
    }

    private fun observeData() {
        val viewModel = viewModel ?: return

        viewModel.speakText.observe(
            viewLifecycleOwner,
            EventObserver {
                Timber.d("speakText: $it")
                tts.textToSpeech(it)
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
    }

    private fun exerciseIncorrect() {
        toastExerciseIncorrect.show()
        playSound.playIncorrect()
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
    }

    private fun processRecognizedText(bundle: Bundle) {
        val viewModel = viewModel ?: return
        val results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return

        Timber.d("Recognized results: $results")

        viewModel.validatePhrase(results)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(PERMISSION_REQUEST_RECORD_AUDIO)
    private fun startRecognizeSpeech() {

        val perms = Manifest.permission.RECORD_AUDIO
        if (EasyPermissions.hasPermissions(requireContext(), perms)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName)
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
            }
            speechRecognizer.startListening(intent)

            btnSpeakPhrase.visibility = View.INVISIBLE
            recognitionProgressView.visibility = View.VISIBLE
            recognitionProgressView.play()
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.msg_rationale_permission_record_audio),
                PERMISSION_REQUEST_RECORD_AUDIO, perms
            )
        }
    }

}

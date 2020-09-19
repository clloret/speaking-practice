package com.clloret.speakingpractice.exercise.practice

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.MainViewModel
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeFragmentBinding
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterStrategy
import com.clloret.speakingpractice.utils.PreferenceValues
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
    }

    private val speechRecognizer: SpeechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(
            requireContext()
        )
    }
    private var tts: TextToSpeech? = null
    private var soundPool: SoundPool? = null
    private var soundCorrect: Int? = null
    private var soundIncorrect: Int? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val preferenceValues: PreferenceValues by inject()
    private var viewModel: PracticeViewModel? = null
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
            setRotationRadiusInDp(20)
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
                        }, 2000)
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
                textToSpeech(it)
            })

        viewModel.exerciseResult.observe(viewLifecycleOwner, {

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (it) {
                PracticeViewModel.ExerciseResult.CORRECT -> exerciseCorrect()
                PracticeViewModel.ExerciseResult.INCORRECT -> exerciseIncorrect()
            }
        })

        viewModel.onClickRecognizeSpeechBtn = {
            btnSpeakPhrase.visibility = View.INVISIBLE
            recognitionProgressView.visibility = View.VISIBLE
            startRecognizeSpeech()
        }
    }

    private fun exerciseCorrect() {
        toastExerciseCorrect.show()
        playSound(soundCorrect)
    }

    private fun exerciseIncorrect() {
        toastExerciseIncorrect.show()
        playSound(soundIncorrect)
    }

    private fun playSound(soundId: Int?) {
        if (!preferenceValues.isSoundEnabled()) {
            return
        }

        soundId?.let {
            soundPool?.play(it, 1.0F, 1.0F, 1, 0, 1F)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initSoundPool()
        initTts()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stopTts()
        stopSoundPool()
    }

    private fun showMessage(message: String) {
        mainViewModel.showMessage(message)
    }

    private fun processRecognizedText(bundle: Bundle) {
        val viewModel = viewModel ?: return
        val results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return

        Timber.d("Recognized results: $results")

        viewModel.validatePhrase(results)
    }

    private fun startRecognizeSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().packageName)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
        }
        speechRecognizer.startListening(intent)

        recognitionProgressView.play()
    }

    private fun initTts() {
        val appContext = this.requireActivity().applicationContext
        val googleTtsPackage = "com.google.android.tts"

        tts = TextToSpeech(appContext, { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ttsLang = tts?.setLanguage(Locale.US)
                if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                    || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    Timber.e("The language is not supported!")
                } else {
                    Timber.i("Language supported.")
                }
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.0f)
                Timber.i("TTS initialization success.")
            } else {

                showMessage(getString(R.string.msg_error_initialization_failed))
            }
        }, googleTtsPackage)
    }

    private fun stopTts() {
        tts?.stop()
        tts?.shutdown()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundCorrect = soundPool?.load(requireContext(), R.raw.sound_exercise_correct, 1)
        soundIncorrect = soundPool?.load(requireContext(), R.raw.sound_exercise_incorrect, 1)
    }

    private fun stopSoundPool() {
        soundPool?.release()
        soundPool = null
    }

    private fun textToSpeech(text: String) {
        val speechStatus = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        if (speechStatus == TextToSpeech.ERROR) {
            showMessage(getString(R.string.msg_error_tts))
        }
    }
}

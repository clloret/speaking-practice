package com.clloret.speakingpractice.exercise.practice

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.MainViewModel
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.PracticeFragmentBinding
import com.clloret.speakingpractice.utils.PreferenceValues
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import java.util.*

class PracticeFragment : BaseFragment() {

    companion object {
        fun newInstance() = PracticeFragment()

        private const val REQUEST_CODE_SPEECH_INPUT = 0x01
    }

    private var tts: TextToSpeech? = null
    private var soundPool: SoundPool? = null
    private var soundCorrect: Int? = null
    private var soundIncorrect: Int? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: PracticeViewModel by viewModel { parametersOf(args.filter) }
    private val args: PracticeFragmentArgs by navArgs()
    private val preferenceValues: PreferenceValues by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
        setupViewPager(binding.viewPager)

        observeData()

        return binding.root
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        val listAdapter = PracticeAdapter(viewModel)
        viewPager.adapter = listAdapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                Timber.d("Page: $position")

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
        viewModel.speakText.observe(
            viewLifecycleOwner,
            EventObserver {
                Timber.d("speakText: $it")
                textToSpeech(it)
            })

        viewModel.exerciseResult.observe(viewLifecycleOwner, {

            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (it) {
                PracticeViewModel.ExerciseResult.CORRECT -> playSound(soundCorrect)
                PracticeViewModel.ExerciseResult.INCORRECT -> playSound(soundIncorrect)
            }
        })

        viewModel.onClickRecognizeSpeechBtn = {
            startRecognizeSpeech()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                processRecognizedText(data)
            }
        }
    }

    private fun processRecognizedText(data: Intent) {
        val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) ?: return

        Timber.d("Recognized results: $results")

        viewModel.validatePhrase(results)
    }

    private fun startRecognizeSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent
            .putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.msg_read_exercise_sentence)
        )
        try {
            startActivityForResult(
                intent,
                REQUEST_CODE_SPEECH_INPUT
            )
        } catch (a: ActivityNotFoundException) {
            showMessage(getString(R.string.msg_error_voice_recognition_not_supported))
        }
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

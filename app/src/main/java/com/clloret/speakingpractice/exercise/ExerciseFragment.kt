package com.clloret.speakingpractice.exercise

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.clloret.speakingpractice.MainViewModel
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseFragmentBinding
import com.clloret.speakingpractice.exercise.add.AddExerciseViewModel
import com.clloret.speakingpractice.utils.Dialogs
import kotlinx.android.synthetic.main.exercise_fragment.*
import timber.log.Timber
import java.util.*

class ExerciseFragment : Fragment() {
    private var tts: TextToSpeech? = null

    companion object {
        fun newInstance() =
            ExerciseFragment()

        private const val REQUEST_CODE_SPEECH_INPUT = 0x01
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: ExerciseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        val binding: ExerciseFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.exercise_fragment, container, false
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeData(savedInstanceState)

        initTts()

        fabRecognizeSpeech.setOnClickListener {
            startRecognizeSpeech()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_exercise, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> addExercise()
            R.id.action_edit -> editExercise()
            R.id.action_delete -> deleteExercise()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeData(savedInstanceState: Bundle?) {
        viewModel.allExercises.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { exercises ->
                exercises?.let {
                    Timber.d("Set exercise data")
                    viewModel.phrases = it

                    if (savedInstanceState == null) {
                        viewModel.loadNextExercise()
                    }
                }
            })

        viewModel.exerciseResult.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                showExerciseResult(it)
            }
        )

        viewModel.speakText.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                textToSpeech(it)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stopTts()
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
        val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) ?: return
        val recognizedText = result[0]

        Timber.i(recognizedText)

        viewModel.validatePhrase(recognizedText)
    }

    private fun showMessage(message: String) {
        mainViewModel.showMessage(message)
    }

    private fun showExerciseResult(result: ExerciseViewModel.ExerciseResult) {
        if (result == ExerciseViewModel.ExerciseResult.HIDDEN) {
            exerciseResultView.visibility = View.GONE
            return
        } else {
            exerciseResultView.visibility = View.VISIBLE
        }

        val image =
            when (result) {
                ExerciseViewModel.ExerciseResult.CORRECT -> R.drawable.ic_check_circle_green_24dp
                ExerciseViewModel.ExerciseResult.INCORRECT -> R.drawable.ic_error_red_24dp
                else -> R.drawable.ic_error_red_24dp
            }
        exerciseResultView.setImageResource(image)
        exerciseResultView.visibility = View.VISIBLE
    }

    private fun startRecognizeSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent
            .putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            getString(R.string.msg_read_exercise_sentence)
        )
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            showMessage(getString(R.string.msg_error_voice_recognition_not_supported))
        }
    }

    private fun initTts() {
        val appContext = this.requireActivity().applicationContext
        val googleTtsPackage = "com.google.android.tts"

        tts = TextToSpeech(appContext, TextToSpeech.OnInitListener { status ->
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

    private fun textToSpeech(text: String) {
        val speechStatus = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        if (speechStatus == TextToSpeech.ERROR) {
            showMessage(getString(R.string.msg_error_tts))
        }
    }

    private fun addExercise(): Boolean {

        val action =
            ExerciseFragmentDirections.actionExerciseFragmentToAddExerciseFragment(
                AddExerciseViewModel.DEFAULT_ID,
                getString(R.string.title_add)
            )

        findNavController()
            .navigate(action)

        return true
    }

    private fun editExercise(): Boolean {

        val exerciseId = viewModel.currentExercise.value?.id ?: return true
        val action =
            ExerciseFragmentDirections.actionExerciseFragmentToAddExerciseFragment(
                exerciseId,
                getString(R.string.title_edit)
            )

        findNavController()
            .navigate(action)

        return true
    }

    private fun deleteExercise(): Boolean {
        Dialogs(requireContext())
            .showConfirmation(messageId = R.string.msg_delete_exercise_confirmation) { result ->
                if (result == Dialogs.Button.POSITIVE) {
                    viewModel.deleteCurrentExercise()
                }
            }
        return true
    }

}

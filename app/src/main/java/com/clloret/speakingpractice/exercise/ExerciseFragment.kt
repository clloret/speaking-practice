package com.clloret.speakingpractice.exercise

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.clloret.speakingpractice.MainViewModel
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseFragmentBinding
import kotlinx.android.synthetic.main.exercise_fragment.*
import timber.log.Timber
import java.util.*

class ExerciseFragment : Fragment() {

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

        viewModel.allExercises.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { exercises ->
                exercises?.let {
                    Timber.d("Set exercise data")
                    viewModel.phrases = it
                    viewModel.loadNextExercise()
                }
            })

        viewModel.exerciseResult.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                showExerciseResult(it)
            }
        )

        fab.setOnClickListener {
            startRecognizeSpeech()
        }
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


}

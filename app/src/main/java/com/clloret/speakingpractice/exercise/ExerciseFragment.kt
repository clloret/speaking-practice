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
import androidx.fragment.app.viewModels
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.exercise_fragment.*
import timber.log.Timber
import java.util.*

class ExerciseFragment : Fragment() {

    companion object {
        fun newInstance() =
            ExerciseFragment()

        private const val REQUEST_CODE_SPEECH_INPUT = 0x01
    }

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

        val validatePhrase = viewModel.validatePhrase(recognizedText)

        val message = if (validatePhrase) "Great!!!" else "Oops, something didn't go right"
        showSnackBar(message)
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
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
            showSnackBar(getString(R.string.msg_error_voice_recognition_not_supported))
        }
    }


}

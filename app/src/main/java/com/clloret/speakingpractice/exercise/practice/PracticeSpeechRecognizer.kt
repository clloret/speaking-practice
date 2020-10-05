package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS
import android.view.View
import androidx.core.content.ContextCompat
import com.clloret.speakingpractice.R
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter
import timber.log.Timber
import java.util.*

class PracticeSpeechRecognizer(
    private val context: Context,
    private val onResults: (ArrayList<String>) -> Unit,
    private val showMessage: (String) -> Unit
) {
    private val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)
    private var progressView: RecognitionProgressView? = null
    private var buttonSpeak: View? = null

    private fun processRecognizedText(bundle: Bundle) {
        val results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return

        Timber.d("Recognized results: $results")

        onResults(results)
    }

    fun setupViews(progressView: RecognitionProgressView, buttonSpeak: View) {
        this.progressView = progressView
        this.buttonSpeak = buttonSpeak

        setupRecognizerProgressView()
    }

    private fun setupRecognizerProgressView() {
        progressView?.apply {
            setSingleColor(ContextCompat.getColor(context, R.color.color_secondary))
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
                            buttonSpeak?.visibility = View.VISIBLE
                        }, RECOGNIZER_PROGRESS_HIDE_DELAY)
                    }
                }

                override fun onError(error: Int) {
                    super.onError(error)

                    Timber.d("onError - error: $error")

                    if (error == ERROR_INSUFFICIENT_PERMISSIONS) {
                        showMessage(context.getString(R.string.msg_error_speech_recognizer_insufficient_permissions))
                    }

                    visibility = View.GONE
                    buttonSpeak?.visibility = View.VISIBLE
                }
            })
            visibility = View.GONE
            play()
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
        }
        speechRecognizer.startListening(intent)

        buttonSpeak?.visibility = View.INVISIBLE
        progressView?.visibility = View.VISIBLE
        progressView?.play()
    }

    fun destroy() {
        speechRecognizer.destroy()
    }

    companion object {
        private const val RECOGNIZER_PROGRESS_ROTATION_RADIUS = 20
        private const val RECOGNIZER_PROGRESS_HIDE_DELAY = 2000L
    }
}


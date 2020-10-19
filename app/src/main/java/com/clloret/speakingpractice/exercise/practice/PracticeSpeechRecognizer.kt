package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.*
import android.view.View
import androidx.core.content.ContextCompat
import com.clloret.speakingpractice.R
import com.github.zagum.speechrecognitionview.RecognitionProgressView
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import java.util.*

class PracticeSpeechRecognizer(
    private val context: Context,
    private val onResults: (ArrayList<String>) -> Unit,
    private val showMessage: (String) -> Unit
) {
    private val speechRecognizer: SpeechRecognizer = createSpeechRecognizer(context)
    private var progressView: RecognitionProgressView? = null
    private var buttonSpeak: View? = null

    private fun processRecognizedText(bundle: Bundle) {
        val results = bundle.getStringArrayList(RESULTS_RECOGNITION) ?: return

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

                    sendErrorToCrashlytics(error)

                    visibility = View.GONE
                    buttonSpeak?.visibility = View.VISIBLE
                }
            })
            visibility = View.GONE
            play()
        }
    }

    private fun sendErrorToCrashlytics(error: Int) {
        if (error in CRASHLYTICS_SEND_ERRORS) {
            FirebaseCrashlytics.getInstance()
                .recordException(SpeechRecognizerException(error))
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
        private val CRASHLYTICS_SEND_ERRORS = arrayOf(
            ERROR_NETWORK_TIMEOUT,
            ERROR_NETWORK,
            ERROR_AUDIO,
            ERROR_SERVER,
            ERROR_CLIENT,
            ERROR_INSUFFICIENT_PERMISSIONS
        )
    }

    class SpeechRecognizerException(error: Int) :
        Exception(getMessageFromError(error) + " Error: $error") {
        companion object {
            private fun getMessageFromError(error: Int): String {
                return when (error) {
                    ERROR_NETWORK_TIMEOUT -> "Network operation timed out."
                    ERROR_NETWORK -> "Other network related errors."
                    ERROR_AUDIO -> "Audio recording error."
                    ERROR_SERVER -> "Server sends error status."
                    ERROR_CLIENT -> "Other client side errors."
                    ERROR_SPEECH_TIMEOUT -> "No speech input."
                    ERROR_NO_MATCH -> "No recognition result matched."
                    ERROR_RECOGNIZER_BUSY -> "RecognitionService busy."
                    ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions."
                    else -> "Unknown error"
                }
            }
        }
    }
}

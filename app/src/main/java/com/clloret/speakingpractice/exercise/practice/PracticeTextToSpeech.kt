package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import android.os.Handler
import android.speech.tts.TextToSpeech
import com.clloret.speakingpractice.R
import timber.log.Timber
import java.util.*

class PracticeTextToSpeech(
    private val context: Context,
    private val showMessage: (String) -> Unit
) {
    enum class Status {
        INITIALIZING, INITIALIZED, ERROR
    }

    private val resources = context.resources
    private var tts: TextToSpeech? = null
    private var status = Status.INITIALIZING

    fun initTts() {
        val googleTtsPackage = "com.google.android.tts"

        tts = TextToSpeech(context, { status ->
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
                this.status = Status.INITIALIZED
                Timber.i("TTS initialization success.")
            } else {
                this.status = Status.ERROR
                showMessage(resources.getString(R.string.msg_error_initialization_failed))
            }
        }, googleTtsPackage)
    }

    fun stopTts() {
        tts?.stop()
        tts?.shutdown()
    }

    fun textToSpeech(text: String) {
        Timber.d("textToSpeech - $text")

        when (status) {
            Status.INITIALIZED -> {
                val speechStatus = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

                if (speechStatus == TextToSpeech.ERROR) {
                    showMessage(resources.getString(R.string.msg_error_tts))
                }
            }
            Status.INITIALIZING -> {
                Handler().postDelayed({
                    textToSpeech(text)
                }, TTS_DELAY_IN_MILLIS)
            }
            Status.ERROR -> {
                Timber.w("The TTS engine could not be initialized")
                return
            }
        }
    }

    companion object {
        private const val TTS_DELAY_IN_MILLIS = 1000L
    }

}

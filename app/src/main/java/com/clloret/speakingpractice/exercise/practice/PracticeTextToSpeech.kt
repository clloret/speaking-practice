package com.clloret.speakingpractice.exercise.practice

import android.content.Context
import android.speech.tts.TextToSpeech
import com.clloret.speakingpractice.R
import timber.log.Timber
import java.util.*

class PracticeTextToSpeech(
    private val context: Context,
    private val showMessage: (String) -> Unit
) {
    private val resources = context.resources
    private var tts: TextToSpeech? = null

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
                Timber.i("TTS initialization success.")
            } else {

                showMessage(resources.getString(R.string.msg_error_initialization_failed))
            }
        }, googleTtsPackage)
    }

    fun stopTts() {
        tts?.stop()
        tts?.shutdown()
    }

    fun textToSpeech(text: String) {
        val speechStatus = tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        if (speechStatus == TextToSpeech.ERROR) {
            showMessage(resources.getString(R.string.msg_error_tts))
        }
    }

}

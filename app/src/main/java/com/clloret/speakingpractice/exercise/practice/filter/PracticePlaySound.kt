package com.clloret.speakingpractice.exercise.practice.filter

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.domain.PreferenceValues

class PracticePlaySound(
    private val context: Context,
    private val preferenceValues: PreferenceValues
) {
    private var soundPool: SoundPool? = null
    private var soundCorrect: Int? = null
    private var soundIncorrect: Int? = null

    private fun playSound(soundId: Int?) {
        if (!preferenceValues.isSoundEnabled()) {
            return
        }

        soundId?.let {
            soundPool?.play(it, 1.0F, 1.0F, 1, 0, 1F)
        }
    }

    fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundCorrect = soundPool?.load(context, R.raw.sound_exercise_correct, 1)
        soundIncorrect = soundPool?.load(context, R.raw.sound_exercise_incorrect, 1)
    }

    fun stopSoundPool() {
        soundPool?.release()
        soundPool = null
    }

    fun playCorrect() {
        playSound(soundCorrect)
    }

    fun playIncorrect() {
        playSound(soundIncorrect)
    }
}

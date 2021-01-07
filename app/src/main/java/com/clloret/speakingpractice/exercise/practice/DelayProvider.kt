package com.clloret.speakingpractice.exercise.practice

interface DelayProvider {
    suspend fun delay(timeMillis: Long)
}

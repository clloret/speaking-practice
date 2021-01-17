package com.clloret.speakingpractice.domain

interface DelayProvider {
    suspend fun delay(timeMillis: Long)
}

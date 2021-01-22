package com.clloret.speakingpractice.utils

object TimeUtils {
    private const val SECONDS_PER_MINUTE = 60
    private const val SECONDS_PER_HOUR = 3_600

    fun secondsToTime(seconds: Int): String {
        val hours = seconds / SECONDS_PER_HOUR
        val minutes = (seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE

        return "%02d:%02d".format(hours, minutes)
    }
}

package com.clloret.speakingpractice.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalcDailyStreak {

  fun calcCurrentStreak(currentDate: LocalDate, storedDate: LocalDate, storedStreak: Int): Int {

    return when (ChronoUnit.DAYS.between(storedDate, currentDate).toInt()) {
      0 -> {
        storedStreak
      }
      1 -> {
        storedStreak + 1
      }
      else -> {
        1
      }
    }
  }

  fun calcLongStreak(storedLongStreak: Int, currentStreak: Int): Int {
    return storedLongStreak.coerceAtLeast(currentStreak)
  }
}

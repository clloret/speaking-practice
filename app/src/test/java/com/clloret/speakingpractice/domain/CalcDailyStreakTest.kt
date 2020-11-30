package com.clloret.speakingpractice.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class CalcDailyStreakTest {
    private val currentDate = LocalDate.now()

    @Test
    fun `when stored date is today`() {
        val sut = CalcDailyStreak()

        val storedDate = currentDate
        val storedStreak = 10
        val calc = sut.calcCurrentStreak(currentDate, storedDate, storedStreak)

        assertThat(calc).isEqualTo(10)
    }

    @Test
    fun `when stored date is yesterday`() {
        val sut = CalcDailyStreak()

        val storedDate = currentDate.minusDays(1)
        val storedStreak = 10
        val calc = sut.calcCurrentStreak(currentDate, storedDate, storedStreak)

        assertThat(calc).isEqualTo(11)
    }

    @Test
    fun `when stored date is more than one day`() {
        val sut = CalcDailyStreak()

        val storedDate = currentDate.minusDays(2)
        val storedStreak = 10
        val calc = sut.calcCurrentStreak(currentDate, storedDate, storedStreak)

        assertThat(calc).isEqualTo(1)
    }

    @Test
    fun `when stored long streak is greater than`() {
        val sut = CalcDailyStreak()

        val storedLongStreak = 20
        val currentStreak = 10
        val result = sut.calcLongStreak(storedLongStreak, currentStreak)

        assertThat(result).isEqualTo(20)
    }

    @Test
    fun `when stored long streak is less than`() {
        val sut = CalcDailyStreak()

        val storedLongStreak = 20
        val currentStreak = 21
        val result = sut.calcLongStreak(storedLongStreak, currentStreak)

        assertThat(result).isEqualTo(21)
    }

}

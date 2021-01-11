package com.clloret.speakingpractice.domain.attempt.criteria

import com.clloret.speakingpractice.domain.entities.AttemptWithExercise

interface AttemptCriteria {
    fun meetCriteria(list: List<AttemptWithExercise>): List<AttemptWithExercise>
}

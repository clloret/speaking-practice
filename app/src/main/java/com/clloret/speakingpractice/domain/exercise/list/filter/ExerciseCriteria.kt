package com.clloret.speakingpractice.domain.exercise.list.filter

import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

interface ExerciseCriteria {
    fun meetCriteria(list: List<ExerciseWithDetails>): List<ExerciseWithDetails>
}
package com.clloret.speakingpractice.domain.exercise.list.filter

import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class ExerciseCriteriaByText(private val text: String) : ExerciseCriteria {
    companion object {
        const val KEY = "text"
    }

    override fun meetCriteria(list: List<ExerciseWithDetails>): List<ExerciseWithDetails> {
        return list.filter { it.practicePhrase.contains(text, ignoreCase = true) }
    }
}

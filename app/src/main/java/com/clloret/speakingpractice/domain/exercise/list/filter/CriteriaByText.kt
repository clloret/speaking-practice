package com.clloret.speakingpractice.domain.exercise.list.filter

import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class CriteriaByText(private val text: String) : Criteria {
    companion object {
        const val KEY = "text"
    }

    override fun meetCriteria(list: List<ExerciseWithDetails>): List<ExerciseWithDetails> {
        return list.filter { it.practicePhrase.contains(text, ignoreCase = true) }
    }
}

package com.clloret.speakingpractice.domain.exercise.list.filter

import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class CriteriaByTag(private val tagIds: List<Int>) : Criteria {
    companion object {
        const val KEY = "tag"
    }

    override fun meetCriteria(list: List<ExerciseWithDetails>): List<ExerciseWithDetails> {
        return list.filter { exercise -> exercise.tags.map { it.id }.any { it in tagIds } }
    }
}

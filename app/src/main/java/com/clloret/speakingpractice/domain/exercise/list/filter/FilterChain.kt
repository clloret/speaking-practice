package com.clloret.speakingpractice.domain.exercise.list.filter

import com.clloret.speakingpractice.domain.entities.ExerciseWithDetails

class FilterChain : Criteria {
    private val filters = mutableMapOf<String, Criteria>()

    override fun meetCriteria(list: List<ExerciseWithDetails>): List<ExerciseWithDetails> {
        var filtered = list
        filters.values.forEach {
            filtered = it.meetCriteria(filtered)
        }
        return filtered
    }

    fun addFilter(key: String, filter: Criteria) {
        filters[key] = filter
    }

    fun removeFilter(key: String) {
        filters.remove(key)
    }
}

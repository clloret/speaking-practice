package com.clloret.speakingpractice.domain.attempt.criteria

import com.clloret.speakingpractice.domain.common.criteria.Criteria
import com.clloret.speakingpractice.domain.entities.AttemptWithExercise

class AttemptCriteriaByResult(private val result: Result) : Criteria<AttemptWithExercise> {
    enum class Result {
        CORRECT, INCORRECT, INDISTINCT
    }

    override fun meetCriteria(list: List<AttemptWithExercise>): List<AttemptWithExercise> {
        if (result == Result.INDISTINCT) {
            return list
        }

        return list.filter {
            it.attempt.result == (result == Result.CORRECT)
        }
    }
}

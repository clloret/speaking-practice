package com.clloret.speakingpractice.domain.common.criteria

interface Criteria<T> {
    fun meetCriteria(list: List<T>): List<T>
}

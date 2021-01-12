package com.clloret.speakingpractice.domain.common.criteria

class FilterChain<T> : Criteria<T> {
    private val filters = mutableMapOf<String, Criteria<T>>()

    override fun meetCriteria(list: List<T>): List<T> {
        var filtered = list
        filters.values.forEach {
            filtered = it.meetCriteria(filtered)
        }
        return filtered
    }

    fun addFilter(key: String, filter: Criteria<T>) {
        filters[key] = filter
    }

    fun removeFilter(key: String) {
        filters.remove(key)
    }
}

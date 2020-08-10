package com.clloret.speakingpractice.domain.word

class WordSortBySuccessRate(orderType: OrderType) : WordSortStrategy(orderType) {
    override fun compare(sortable1: WordSortable, sortable2: WordSortable): Int {
        val s1: WordSortable = getFirst(sortable1, sortable2)
        val s2: WordSortable = getSecond(sortable1, sortable2)
        return s1.successRate.compareTo(s2.successRate)
    }
}

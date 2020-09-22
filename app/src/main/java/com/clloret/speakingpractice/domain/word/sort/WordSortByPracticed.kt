package com.clloret.speakingpractice.domain.word.sort

class WordSortByPracticed(orderType: OrderType) : WordSortStrategy(orderType) {
    override fun compare(sortable1: WordSortable, sortable2: WordSortable): Int {
        val s1: WordSortable = getFirst(sortable1, sortable2)
        val s2: WordSortable = getSecond(sortable1, sortable2)
        return s1.totalPracticed.compareTo(s2.totalPracticed)
    }
}

private val WordSortable.totalPracticed: Int
    get() = correct + incorrect

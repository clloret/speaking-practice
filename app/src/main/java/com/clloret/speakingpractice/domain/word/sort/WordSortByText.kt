package com.clloret.speakingpractice.domain.word.sort

class WordSortByText(orderType: OrderType) : WordSortStrategy(orderType) {
    override fun compare(sortable1: WordSortable, sortable2: WordSortable): Int {
        val s1: WordSortable = getFirst(sortable1, sortable2)
        val s2: WordSortable = getSecond(sortable1, sortable2)
        return s1.word.compareTo(s2.word)
    }
}

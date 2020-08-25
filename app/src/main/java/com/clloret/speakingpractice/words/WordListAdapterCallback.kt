package com.clloret.speakingpractice.words

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults
import com.clloret.speakingpractice.domain.word.sort.WordSortable

class WordListAdapterCallback(
    adapter: RecyclerView.Adapter<*>,
    var comparator: Comparator<WordSortable>
) :
    SortedListAdapterCallback<PracticeWordWithResults>(adapter) {

    override fun areItemsTheSame(
        item1: PracticeWordWithResults,
        item2: PracticeWordWithResults
    ): Boolean {
        return item1.word == item2.word
    }

    override fun compare(o1: PracticeWordWithResults, o2: PracticeWordWithResults): Int {
        return comparator.compare(o1, o2)
    }

    override fun areContentsTheSame(
        oldItem: PracticeWordWithResults,
        newItem: PracticeWordWithResults
    ): Boolean {
        return oldItem == newItem
    }
}

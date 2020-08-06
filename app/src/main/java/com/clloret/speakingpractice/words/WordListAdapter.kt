package com.clloret.speakingpractice.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListItemBinding
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults
import com.clloret.speakingpractice.domain.word.WordSortable

class WordListAdapter(comparator: Comparator<WordSortable>) :
    RecyclerView.Adapter<WordListViewHolder>() {

    private val adapterCallback: WordListAdapterCallback =
        WordListAdapterCallback(this, comparator)
    private val sortedList: SortedList<PracticeWordWithResults> =
        SortedList<PracticeWordWithResults>(
            PracticeWordWithResults::class.java,
            adapterCallback
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: WordListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.word_list_item,
            parent, false
        )
        return WordListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordListViewHolder, position: Int) {
        val item = sortedList.get(position)
        holder.bind(item)
    }

    override fun getItemCount() = sortedList.size()

    fun submitList(list: Collection<PracticeWordWithResults>) {
        sortedList.replaceAll(list)
    }

    fun setOrder(comparator: Comparator<WordSortable>) {
        adapterCallback.comparator = comparator

        with(sortedList) {
            beginBatchedUpdates()

            val list = (0 until sortedList.size())
                .mapTo(ArrayList<PracticeWordWithResults>()) { get(it) }
            replaceAll(list)

            endBatchedUpdates()
        }
    }
}

package com.clloret.speakingpractice.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListItemBinding
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterByWord
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults
import com.clloret.speakingpractice.domain.word.WordSortable

class WordListAdapter(
    comparator: Comparator<WordSortable>,
    private val findNavController: NavController
) :
    RecyclerView.Adapter<WordListAdapter.ViewHolder>(), OnClickWordHandler {

    private val adapterCallback: WordListAdapterCallback =
        WordListAdapterCallback(this, comparator)
    private val sortedList: SortedList<PracticeWordWithResults> =
        SortedList<PracticeWordWithResults>(
            PracticeWordWithResults::class.java,
            adapterCallback
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: WordListItemBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.word_list_item,
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sortedList.get(position)
        holder.bind(item, this)
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

    override fun onClick(word: PracticeWordWithResults) {
        val action = WordListFragmentDirections.actionWordListFragmentToAttemptListFragment(
            filter = AttemptFilterByWord(word.word)
        )
        findNavController.navigate(action)
    }

    class ViewHolder(private val binding: WordListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: PracticeWordWithResults,
            onClickHandler: OnClickWordHandler
        ) {
            binding.apply {
                word = item
                handler = onClickHandler
            }
        }
    }

}

interface OnClickWordHandler {
    fun onClick(word: PracticeWordWithResults)
}

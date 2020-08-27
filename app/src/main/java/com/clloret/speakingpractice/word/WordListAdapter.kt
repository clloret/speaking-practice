package com.clloret.speakingpractice.word

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.WordListItemBinding
import com.clloret.speakingpractice.domain.attempt.filter.AttemptFilterByWord
import com.clloret.speakingpractice.domain.entities.PracticeWordWithResults
import com.clloret.speakingpractice.domain.exercise.filter.ExerciseFilterByWord
import com.clloret.speakingpractice.domain.word.sort.WordSortable

class WordListAdapter(
    comparator: Comparator<WordSortable>,
    private val findNavController: NavController
) :
    RecyclerView.Adapter<WordListAdapter.ViewHolder>(), OnClickWordHandler {

    private val adapterCallback: WordListAdapterCallback =
        WordListAdapterCallback(this, comparator)
    private val sortedList: SortedList<PracticeWordWithResults> =
        SortedList(
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
        val action = WordListFragmentDirections.actionWordListFragmentToPracticeFragment(
            ExerciseFilterByWord(word.word)
        )
        findNavController.navigate(action)
    }

    override fun onClickMenu(view: View, word: PracticeWordWithResults) {
        PopupMenu(view.context, view).apply {
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_show_attempts -> showExerciseAttempts(word)
                    else -> false
                }
            }
            inflate(R.menu.menu_word_list_popup)
            gravity = Gravity.START
            show()
        }
    }

    private fun showExerciseAttempts(word: PracticeWordWithResults): Boolean {
        val action = WordListFragmentDirections.actionWordListFragmentToAttemptListFragment(
            AttemptFilterByWord(word.word)
        )
        findNavController.navigate(action)

        return true
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
    fun onClickMenu(view: View, word: PracticeWordWithResults)
}

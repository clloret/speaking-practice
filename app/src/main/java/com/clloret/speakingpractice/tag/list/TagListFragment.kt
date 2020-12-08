package com.clloret.speakingpractice.tag.list

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.TagListFragmentBinding
import com.clloret.speakingpractice.tag.add.AddTagViewModel
import com.clloret.speakingpractice.utils.Dialogs
import com.clloret.speakingpractice.utils.RecyclerViewEmptyObserver
import com.clloret.speakingpractice.utils.selection.LongItemDetailsLookup
import com.clloret.speakingpractice.utils.selection.LongItemKeyProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class TagListFragment : BaseFragment(), TagListAdapter.TagListListener {

    companion object {
        fun newInstance() = TagListFragment()
    }

    private val viewModel: TagListViewModel by viewModel()
    private var selectionTracker: SelectionTracker<Long>? = null
    private var actionMode: ActionMode? = null
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.menu_exercise_list_selected, menu)
            mode.title = getString(R.string.title_selection)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            Timber.d("onPrepareActionMode")

            selectionTracker?.selection?.size()?.let { count ->
                val menuItem = menu.findItem(R.id.action_edit)
                menuItem.isVisible = count == 1

                return true
            }

            return false // Return false if nothing is done
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_edit -> {
                    editSelectedTag()
                    mode.finish()
                    true
                }
                R.id.action_delete -> {
                    deleteSelectedTags()
                    mode.finish()
                    true
                }
                else -> false
            }
        }

        // Called when the user exits the action mode
        override fun onDestroyActionMode(mode: ActionMode) {
            selectionTracker?.clearSelection()
            actionMode = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(this) {

            Timber.d("onBackPressedDispatcher")

            this.isEnabled = true

            selectionTracker?.apply {
                if (hasSelection()) {
                    clearSelection()
                } else {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: TagListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.tag_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.setupRecyclerView(binding.emptyView, savedInstanceState)
        binding.fabAddTag.setOnClickListener {
            addTag()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        selectionTracker?.onSaveInstanceState(outState)
    }

    private fun RecyclerView.setupRecyclerView(emptyView: View, savedInstanceState: Bundle?) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            context,
            linearLayoutManager.orientation
        )
        addItemDecoration(dividerItemDecoration)

        val listAdapter = TagListAdapter(this@TagListFragment)
        adapter = listAdapter

        val rvEmptyObserver = RecyclerViewEmptyObserver(this, emptyView)
        listAdapter.registerAdapterDataObserver(rvEmptyObserver)

        viewModel.tags.observe(viewLifecycleOwner, {
            it?.let {
                listAdapter.submitList(it)

                selectionTracker = SelectionTracker.Builder(
                    "tag-selection",
                    this,
                    LongItemKeyProvider(
                        this
                    ),
                    LongItemDetailsLookup(
                        this
                    ),
                    StorageStrategy.createLongStorage()
                ).build()

                selectionTracker?.addObserver(
                    object : SelectionTracker.SelectionObserver<Long>() {
                        override fun onSelectionChanged() {
                            super.onSelectionChanged()

                            Timber.d("onSelectionChanged")

                            if (actionMode == null) {
                                actionMode = activity?.startActionMode(actionModeCallback)
                            }

                            selectionTracker?.selection?.size()?.let { count ->
                                when (count) {
                                    in 1..2 -> actionMode?.invalidate()
                                    0 -> actionMode?.finish()
                                    else -> {
                                        // Do nothing
                                    }
                                }
                            }
                        }
                    })

                listAdapter.selectionTracker = selectionTracker

                if (savedInstanceState != null) {
                    selectionTracker?.onRestoreInstanceState(savedInstanceState)
                }
            }
        })
    }

    private fun addTag(): Boolean {

        val action =
            TagListFragmentDirections.actionTagListFragmentToAddTagFragment(
                AddTagViewModel.DEFAULT_ID,
                getString(R.string.title_add_tag)
            )

        findNavController()
            .navigate(action)

        return true
    }

    private fun editSelectedTag(): Boolean {

        selectionTracker?.apply {
            selection.first()?.let { tagId ->
                onEditTag(tagId.toInt())
            }
        }
        return true
    }

    private fun deleteSelectedTags() {
        selectionTracker?.selection?.apply {
            val list = this.map { it.toInt() }
            deleteTags(R.string.msg_delete_selected_tags_confirmation, list)
        }
    }

    private fun deleteTags(@StringRes messageId: Int, list: List<Int>) {
        Dialogs(requireContext())
            .showConfirmation(messageId = messageId) { result ->
                if (result == Dialogs.Button.POSITIVE) {
                    viewModel.deleteTagList(list)
                }
            }
    }

    override fun onEditTag(tagId: Int) {
        val action =
            TagListFragmentDirections.actionTagListFragmentToAddTagFragment(
                tagId,
                getString(R.string.title_edit_tag)
            )

        findNavController()
            .navigate(action)
    }

    override fun onDeleteTag(tagId: Int) {
        deleteTags(R.string.msg_delete_tag_confirmation, listOf(tagId))
    }
}

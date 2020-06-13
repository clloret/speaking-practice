package com.clloret.speakingpractice.tag.list

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.TagListFragmentBinding
import com.clloret.speakingpractice.tag.add.AddTagViewModel
import com.clloret.speakingpractice.utils.Dialogs
import timber.log.Timber

class TagListFragment : Fragment() {

    companion object {
        fun newInstance() = TagListFragment()
    }

    private val viewModel: TagListViewModel by viewModels()
    var selectionTracker: SelectionTracker<Long>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        requireActivity().onBackPressedDispatcher.addCallback(this) {

            Timber.d("onBackPressedDispatcher")

            this.isEnabled = true

            selectionTracker?.apply {
                if (hasSelection()) {
                    clearSelection()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: TagListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.tag_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.setupRecyclerView(savedInstanceState)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_exercise_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> addTag()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = "Selection"
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.menu_exercise_list_selected, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            Timber.d("onPrepareActionMode")

            selectionTracker?.selection?.size()?.let {
                val menuItem = menu.findItem(R.id.action_edit)
                menuItem.isVisible = it == 1

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
                    deleteSelectedExercises()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        selectionTracker?.onSaveInstanceState(outState)
    }

    private fun deleteSelectedExercises() {
        selectionTracker?.selection?.apply {
            val list = this.map { it.toInt() }

            Dialogs(requireContext())
                .showConfirmation(messageId = R.string.msg_delete_exercise_confirmation) { result ->
                    if (result == Dialogs.Button.POSITIVE) {
                        viewModel.deleteExerciseList(list)
                    }
                }
        }
    }

    private fun RecyclerView.setupRecyclerView(savedInstanceState: Bundle?) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            context,
            linearLayoutManager.orientation
        )
        addItemDecoration(dividerItemDecoration)

        val listAdapter = TagListAdapter(findNavController())
        adapter = listAdapter

        viewModel.tags.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.submitList(it)

                selectionTracker = SelectionTracker.Builder(
                    "tag-selection",
                    this,
                    TagItemKeyProvider(it),
                    ExerciseItemDetailsLookup(this),
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
                                if (count in 1..2) {
                                    actionMode?.invalidate()
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
                getString(R.string.title_add)
            )

        findNavController()
            .navigate(action)

        return true
    }

    private fun editSelectedTag(): Boolean {

        selectionTracker?.apply {
            selection.first()?.let { tagId ->
                val action =
                    TagListFragmentDirections.actionTagListFragmentToAddTagFragment(
                        tagId.toInt(),
                        getString(R.string.title_edit)
                    )

                findNavController()
                    .navigate(action)
            }
        }
        return true
    }

    class ExerciseItemDetailsLookup internal constructor(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(motionEvent: MotionEvent): ItemDetails<Long>? {
            val view: View? = recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
            if (view != null) {
                val viewHolder = recyclerView.getChildViewHolder(view)
                if (viewHolder is TagListViewHolder) {
                    return viewHolder.getItemDetails(
                        motionEvent
                    )
                }
            }
            return null
        }

    }

}
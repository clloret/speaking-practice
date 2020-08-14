package com.clloret.speakingpractice.exercise.list

import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListFragmentBinding
import com.clloret.speakingpractice.exercise.add.AddExerciseViewModel
import com.clloret.speakingpractice.utils.Dialogs
import com.clloret.speakingpractice.utils.RecyclerViewEmptyObserver
import com.clloret.speakingpractice.utils.selection.LongItemDetailsLookup
import com.clloret.speakingpractice.utils.selection.LongItemKeyProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ExerciseListFragment : Fragment() {

    companion object {
        fun newInstance() = ExerciseListFragment()
    }

    private val viewModel: ExerciseListViewModel by viewModel()
    private var selectionTracker: SelectionTracker<Long>? = null

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
    ): View? {
        val binding: ExerciseListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.exercise_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.setupRecyclerView(binding.emptyView, savedInstanceState)

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
            R.id.action_add -> addExercise()
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
                    editSelectedExercise()
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

    private fun RecyclerView.setupRecyclerView(emptyView: View, savedInstanceState: Bundle?) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            context,
            linearLayoutManager.orientation
        )
        addItemDecoration(dividerItemDecoration)

        val listAdapter = ExerciseListAdapter(findNavController())
        adapter = listAdapter

        val rvEmptyObserver = RecyclerViewEmptyObserver(this, emptyView)
        listAdapter.registerAdapterDataObserver(rvEmptyObserver)

        viewModel.exercises.observe(viewLifecycleOwner, Observer {
            it?.let {
                listAdapter.submitList(it)

                selectionTracker = SelectionTracker.Builder(
                    "exercise-selection",
                    this,
                    LongItemKeyProvider(this),
                    LongItemDetailsLookup(this),
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

    private fun addExercise(): Boolean {

        val action =
            ExerciseListFragmentDirections.actionExerciseListFragmentToAddExerciseFragment(
                AddExerciseViewModel.DEFAULT_ID,
                getString(R.string.title_add)
            )

        findNavController()
            .navigate(action)

        return true
    }

    private fun editSelectedExercise(): Boolean {

        selectionTracker?.apply {
            selection.first()?.let { exerciseId ->
                val action =
                    ExerciseListFragmentDirections.actionExerciseListFragmentToAddExerciseFragment(
                        exerciseId.toInt(),
                        getString(R.string.title_edit)
                    )

                findNavController()
                    .navigate(action)
            }
        }
        return true
    }

}

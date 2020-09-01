package com.clloret.speakingpractice.exercise.list

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ExerciseListFragmentBinding
import com.clloret.speakingpractice.domain.exercise.sort.ExerciseSortable
import com.clloret.speakingpractice.exercise.add.AddExerciseViewModel
import com.clloret.speakingpractice.utils.Dialogs
import com.clloret.speakingpractice.utils.RecyclerViewEmptyObserver
import com.clloret.speakingpractice.utils.selection.LongItemDetailsLookup
import com.clloret.speakingpractice.utils.selection.LongItemKeyProvider
import kotlinx.android.synthetic.main.word_list_fragment.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import timber.log.Timber


class ExerciseListFragment : BaseFragment() {

    companion object {
        fun newInstance() = ExerciseListFragment()
    }

    private val viewModel: ExerciseListViewModel by viewModel()
    private val sortByAlphaAsc: Comparator<ExerciseSortable> by inject(named("ExerciseSortByTextAsc"))
    private val sortByAlphaDesc: Comparator<ExerciseSortable> by inject(named("ExerciseSortByTextDesc"))
    private val sortByCorrect: Comparator<ExerciseSortable> by inject(named("ExerciseSortByCorrectDesc"))
    private val sortByIncorrect: Comparator<ExerciseSortable> by inject(named("ExerciseSortByIncorrectDesc"))
    private val sortBySuccessRateAsc: Comparator<ExerciseSortable> by inject(named("ExerciseSortBySuccessRateAsc"))
    private val sortBySuccessRateDesc: Comparator<ExerciseSortable> by inject(named("ExerciseSortBySuccessRateDesc"))
    private val sortByPracticedAsc: Comparator<ExerciseSortable> by inject(named("ExerciseSortByPracticedAsc"))
    private val sortByPracticedDesc: Comparator<ExerciseSortable> by inject(named("ExerciseSortByPracticedDesc"))
    private var selectionTracker: SelectionTracker<Long>? = null

    private var searchView: SearchView? = null

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

        configureSearchView(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        selectStoredSortOrder(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> addExercise()
            R.id.menu_exercise_list_sort_alphabetically_asc -> selectSortMenuItem(
                item,
                sortByAlphaAsc
            )
            R.id.menu_exercise_list_sort_alphabetically_desc -> selectSortMenuItem(
                item,
                sortByAlphaDesc
            )
            R.id.menu_exercise_list_sort_correct -> selectSortMenuItem(item, sortByCorrect)
            R.id.menu_exercise_list_sort_incorrect -> selectSortMenuItem(item, sortByIncorrect)
            R.id.menu_exercise_list_sort_success_rate_asc -> selectSortMenuItem(
                item,
                sortBySuccessRateAsc
            )
            R.id.menu_exercise_list_sort_success_rate_desc -> selectSortMenuItem(
                item,
                sortBySuccessRateDesc
            )
            R.id.menu_exercise_list_sort_more_practiced -> selectSortMenuItem(
                item,
                sortByPracticedDesc
            )
            R.id.menu_exercise_list_sort_less_practiced -> selectSortMenuItem(
                item,
                sortByPracticedAsc
            )
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun selectStoredSortOrder(menu: Menu) {
        val itemId = viewModel.sortItemId ?: R.id.menu_exercise_list_sort_alphabetically_asc
        menu.findItem(itemId).isChecked = true
    }

    private fun configureSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.menu_exercise_search)
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        val activity: FragmentActivity = requireActivity()
        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.let {
            it.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
            viewModel.observeSearchQuery(RxSearchObservable.fromView(it))
        }

    }

    private fun selectSortMenuItem(
        item: MenuItem,
        comparator: Comparator<ExerciseSortable>
    ): Boolean {
        sortBy(comparator)
        item.isChecked = true
        viewModel.selectedComparator = comparator
        viewModel.sortItemId = item.itemId
        return true
    }

    private fun sortBy(comparator: Comparator<ExerciseSortable>) {
        val adapter = recyclerView.adapter as ExerciseListAdapter
        adapter.setOrder(comparator)
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

        val comparator = viewModel.selectedComparator ?: sortByAlphaAsc
        val listAdapter = ExerciseListAdapter(comparator, findNavController())
        adapter = listAdapter

        val rvEmptyObserver = RecyclerViewEmptyObserver(this, emptyView)
        listAdapter.registerAdapterDataObserver(rvEmptyObserver)

        viewModel.exercises.observe(viewLifecycleOwner, {
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

        viewModel.filtered.observe(viewLifecycleOwner, {
            listAdapter.submitList(it)
        })
    }

    private fun addExercise(): Boolean {

        val action =
            ExerciseListFragmentDirections.actionExerciseListFragmentToAddExerciseFragment(
                AddExerciseViewModel.DEFAULT_ID,
                getString(R.string.title_add_exercise)
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
                        getString(R.string.title_edit_exercise)
                    )

                findNavController()
                    .navigate(action)
            }
        }
        return true
    }

}

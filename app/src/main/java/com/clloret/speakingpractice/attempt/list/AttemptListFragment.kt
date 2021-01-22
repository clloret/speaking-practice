package com.clloret.speakingpractice.attempt.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.AttemptListFragmentBinding
import com.clloret.speakingpractice.domain.attempt.criteria.AttemptCriteriaByResult
import com.clloret.speakingpractice.utils.Dialogs
import com.clloret.speakingpractice.utils.RecyclerViewEmptyObserver
import com.clloret.speakingpractice.utils.ScrollToTopButton
import com.google.android.material.chip.ChipGroup
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AttemptListFragment : BaseFragment(), AttemptListAdapter.AttemptListListener {

    companion object {
        fun newInstance() = AttemptListFragment()
    }

    private val viewModel: AttemptListViewModel by viewModel { parametersOf(args.filter) }
    private val args: AttemptListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: AttemptListFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.attempt_list_fragment, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.setup(binding.emptyView)
        binding.chipGroup.setup()
        binding.emptyView.setText(args.emptyTextResId)

        ScrollToTopButton.configure(binding.scrollToTopButton, binding.recyclerView)

        return binding.root
    }

    private fun ChipGroup.setup() {
        setOnCheckedChangeListener { _, checkedId ->
            val filterResult: AttemptCriteriaByResult.Result = when (checkedId) {
                R.id.chipCorrect -> AttemptCriteriaByResult.Result.CORRECT
                R.id.chipIncorrect -> AttemptCriteriaByResult.Result.INCORRECT
                else -> AttemptCriteriaByResult.Result.INDISTINCT
            }
            viewModel.filterByResult(filterResult)
        }
    }

    private fun RecyclerView.setup(emptyView: View) {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            context,
            linearLayoutManager.orientation
        )
        addItemDecoration(dividerItemDecoration)

        val listAdapter = AttemptListAdapter(viewModel, this@AttemptListFragment)
        adapter = listAdapter

        val rvEmptyObserver = RecyclerViewEmptyObserver(this, emptyView)
        listAdapter.registerAdapterDataObserver(rvEmptyObserver)

        viewModel.attempts.observe(viewLifecycleOwner, {
            it?.let {
                listAdapter.submitList(it)
            }
        })
    }

    override fun onDeleteAttempt(attemptId: Int) {
        Dialogs(requireContext())
            .showConfirmation(messageId = R.string.msg_delete_exercise_attempt_confirmation) { result ->
                if (result == Dialogs.Button.POSITIVE) {
                    viewModel.deleteAttempt(attemptId)
                }
            }
    }
}

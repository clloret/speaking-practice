package com.clloret.speakingpractice.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

class RecyclerViewEmptyObserver(
    private val recyclerView: RecyclerView,
    private val emptyView: View
) :
    AdapterDataObserver() {

    init {
        checkIfEmpty()
    }

    private fun checkIfEmpty() {
        recyclerView.adapter?.let {
            val emptyViewVisible = it.itemCount == 0
            emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            recyclerView.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }

    override fun onChanged() {
        checkIfEmpty()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        checkIfEmpty()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        checkIfEmpty()
    }

}
package com.clloret.speakingpractice.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

object ScrollToTopButton {
    private const val ALPHA = 0.75f
    private const val HIDE_ANIMATION_DELAY = 2000L
    private const val HIDE_ANIMATION_DURATION = 1000L

    private val handler = Handler()

    fun configure(scrollToTopButton: View, recyclerView: RecyclerView) {
        val runnable = Runnable {
            Timber.d("on animation runnable")

            scrollToTopButton.animate()
                .alpha(0.0f)
                .setDuration(HIDE_ANIMATION_DURATION)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        scrollToTopButton.visibility = View.GONE
                    }
                })
                .start()
        }

        scrollToTopButton.apply {
            alpha = ALPHA
            setOnClickListener {
                handler.removeCallbacks(runnable)
                visibility = View.GONE
                recyclerView.smoothScrollToPosition(0)
            }
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        Timber.d("on scroll start")

                        handler.removeCallbacks(runnable)

                        scrollToTopButton.apply {
                            animate().cancel()
                            alpha = ALPHA
                            visibility = View.VISIBLE
                        }
                    }
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        Timber.d("on scroll end")
                        handler.postDelayed(runnable, HIDE_ANIMATION_DELAY)
                    }
                }
            }
        })
    }
}

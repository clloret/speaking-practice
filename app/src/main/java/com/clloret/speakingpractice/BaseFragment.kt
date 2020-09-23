package com.clloret.speakingpractice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

open class BaseFragment : Fragment() {

    protected open val trackScreenView: Boolean = true

    override fun onResume() {
        super.onResume()

        if (trackScreenView) {
            trackScreen()
        }
    }

    protected fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }
}

fun Fragment.trackScreen() {
    (requireActivity() as AppCompatActivity).supportActionBar?.let {
        Timber.d("trackScreen: ${it.title}")

        val screenName = it.title.toString()
        setCurrentScreen(screenName)
    }
}

fun Fragment.setCurrentScreen(screenName: String) {
    val screenClass = this.javaClass.simpleName
    Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        val analytics = FirebaseAnalytics
            .getInstance(requireActivity().applicationContext)
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
    }
}

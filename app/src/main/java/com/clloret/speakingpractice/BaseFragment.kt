package com.clloret.speakingpractice

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics

open class BaseFragment : Fragment() {

    protected open val trackScreenView: Boolean = true

    override fun onResume() {
        super.onResume()

        if (trackScreenView) {
            trackScreen()
        }
    }

}

fun Fragment.trackScreen() {
    val screenName = findNavController().currentDestination?.let {
        it.label.toString()
    } ?: javaClass.simpleName
    setCurrentScreen(screenName)
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

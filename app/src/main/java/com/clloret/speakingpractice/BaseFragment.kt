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
    Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, javaClass.simpleName)
        val analytics = FirebaseAnalytics
            .getInstance(requireActivity().applicationContext)
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
    }
}

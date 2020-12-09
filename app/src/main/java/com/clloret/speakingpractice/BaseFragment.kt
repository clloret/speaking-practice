package com.clloret.speakingpractice

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
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
        val activity = activity ?: return
        val snackBar = Snackbar.make(
            activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
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
//    Bundle().apply {
//        putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
//        putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
//        val analytics = FirebaseAnalytics
//            .getInstance(requireActivity().applicationContext)
//        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, this)
//    }

    Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        param(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
    }
}

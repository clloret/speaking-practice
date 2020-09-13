package com.clloret.speakingpractice

import android.app.Application
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.utils.PreferenceValues
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

class App : Application() {
    private val preferenceValues: PreferenceValues by inject()
    private val database: AppDatabase by inject()

    override fun onCreate() {
        super.onCreate()

        setupLog()
        setupKoin()
        setupCollectionServices()
        populateDatabase()
    }

    private fun setupKoin() = KoinModule.setupKoin(this)

    private fun populateDatabase() = GlobalScope.launch {
        database.exerciseDao().getRandomExercisesIds(1)
    }

    private fun setupCollectionServices() {
        val enableCollection = !BuildConfig.DEBUG && preferenceValues.isAnalyticsEnabled()
        Timber.d("setupCollectionServices - enable: $enableCollection")
        FirebaseAnalytics
            .getInstance(this)
            .setAnalyticsCollectionEnabled(enableCollection)
        FirebaseCrashlytics
            .getInstance()
            .setCrashlyticsCollectionEnabled(enableCollection)
    }

    private fun setupLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

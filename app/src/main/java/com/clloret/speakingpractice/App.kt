package com.clloret.speakingpractice

import android.app.Application
import android.provider.Settings
import com.clloret.speakingpractice.db.AppDatabase
import com.clloret.speakingpractice.domain.PreferenceValues
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        val date = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.MIN)
        database.exerciseDao().getRandomExercisesIds(date, 1)
    }

    private fun setupCollectionServices() {
        val testLabSetting = Settings.System.getString(contentResolver, "firebase.test.lab")
        val enableCollection =
            !BuildConfig.DEBUG && testLabSetting != "true" && preferenceValues.isAnalyticsEnabled()
        Timber.d("setupCollectionServices - enable: $enableCollection")
        Firebase.analytics.setAnalyticsCollectionEnabled(enableCollection)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(enableCollection)
    }

    private fun setupLog() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

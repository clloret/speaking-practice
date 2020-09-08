package com.clloret.speakingpractice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import com.google.android.material.snackbar.Snackbar
import com.vorlonsoft.android.rate.AppRate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        configureAppBar()
        configureAndroidRate()
        observeData()
    }

    private fun configureAppBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(bottomNavView.menu)
        toolbar.setupWithNavController(navController, appBarConfiguration)
        bottomNavView.setupWithNavController(navController)
    }

    private fun observeData() {
        mainViewModel.message.observe(
            this,
            EventObserver {
                showSnackBar(it)
            })
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    private fun configureAndroidRate() {
        AppRate.with(this)
            .monitor()

        AppRate.showRateDialogIfMeetsConditions(this)
    }

}

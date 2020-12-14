package com.clloret.speakingpractice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.clloret.speakingpractice.databinding.ActivityMainBinding
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import com.google.android.material.snackbar.Snackbar
import com.vorlonsoft.android.rate.AppRate

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setSupportActionBar(ui.toolbar)
        configureAppBar()
        configureAndroidRate()
        observeData()
    }

    private fun configureAppBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavView = ui.main.bottomNavView
        bottomNavView.setupWithNavController(navController)
        val appBarConfiguration = AppBarConfiguration(bottomNavView.menu)
        ui.toolbar.setupWithNavController(navController, appBarConfiguration)
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

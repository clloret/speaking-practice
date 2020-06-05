package com.clloret.speakingpractice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.clloret.speakingpractice.db.ExerciseRepository
import com.clloret.speakingpractice.db.ExercisesDatabase
import com.clloret.speakingpractice.exercise.import_.ImportExercises
import com.clloret.speakingpractice.exercise.practice.PracticeFragmentDirections
import com.clloret.speakingpractice.utils.Dialogs
import com.clloret.speakingpractice.utils.lifecycle.EventObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val repository: ExerciseRepository by lazy {
        initRepository()
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        observeData()
    }

    private fun observeData() {
        mainViewModel.message.observe(
            this,
            EventObserver {
                showSnackBar(it)
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (requestCode == FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            resultData?.data?.also { uri ->
                Dialogs(this)
                    .showConfirmationWithCancel(messageId = R.string.msg_replace_previous_exercises) { result ->
                        if (result != Dialogs.Button.NEUTRAL) {
                            val importExercises =
                                ImportExercises(
                                    repository,
                                    contentResolver
                                )
                            importExercises.import(
                                uri,
                                result == Dialogs.Button.POSITIVE
                            ) { count ->
                                showSnackBar("$count exercises imported successfully")
                            }
                        }
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_import -> performFileSearch()
            R.id.action_exercise_list -> showExerciseList()
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showExerciseList(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val action =
            PracticeFragmentDirections.actionExerciseFragmentToExerciseListFragment()

        navController
            .navigate(action)

        return true
    }

    private fun initRepository(): ExerciseRepository {
        val db = ExercisesDatabase.getDatabase(application, this)
        return ExerciseRepository(db)
    }

    private fun performFileSearch(): Boolean {

        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/csv"
        }

        startActivityForResult(intent, FILE_READ_REQUEST_CODE)

        return true
    }

    private fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )
        snackBar.show()
    }

    companion object {
        private const val FILE_READ_REQUEST_CODE: Int = 0x01
    }

}

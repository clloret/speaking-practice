package com.clloret.speakingpractice.exercise.practice

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ActivityPracticeBinding

class PracticeActivity : AppCompatActivity() {

    private val args: PracticeActivityArgs by navArgs()
    private lateinit var ui: ActivityPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setSupportActionBar(ui.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = args.title

        if (savedInstanceState != null) {
            return
        }

        val fragment = PracticeFragment.newInstance(args.filter)
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

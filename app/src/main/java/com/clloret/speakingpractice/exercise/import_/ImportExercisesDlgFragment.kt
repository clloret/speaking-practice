package com.clloret.speakingpractice.exercise.import_

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.clloret.speakingpractice.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.import_exercises_dlg_fragment.*

class ImportExercisesDlgFragment : BottomSheetDialogFragment(),
    NavigationView.OnNavigationItemSelectedListener {

    private val sharedViewModel: ImportExercisesSharedViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.import_exercises_dlg_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_import_help -> {
                sharedViewModel.showHelp(); return true
            }
            R.id.menu_import_select_file -> {
                sharedViewModel.selectFile(); return true
            }
            else -> false
        }
    }

}
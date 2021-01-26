package com.clloret.speakingpractice.exercise.file.export

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.navGraphViewModels
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.ImportExercisesDlgFragmentBinding
import com.clloret.speakingpractice.exercise.file.common.ImportExportSharedViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

class ExportExercisesDlgFragment : BottomSheetDialogFragment(),
    NavigationView.OnNavigationItemSelectedListener {

    private val sharedViewModel: ImportExportSharedViewModel by navGraphViewModels(R.id.nav_graph)
    private var _ui: ImportExercisesDlgFragmentBinding? = null
    private val ui get() = _ui!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = ImportExercisesDlgFragmentBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ui.navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_export_help -> {
                sharedViewModel.showExportHelp(); true
            }
            R.id.menu_export_select_file -> {
                sharedViewModel.selectFileToSave(); true
            }
            else -> false
        }
    }
}

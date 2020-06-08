package com.clloret.speakingpractice.exercise.list

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup

internal class ExerciseItemDetails : ItemDetailsLookup.ItemDetails<Long>() {
    var pos = 0
    var identifier: Long? = null

    override fun getPosition(): Int {
        return pos
    }

    override fun getSelectionKey(): Long? {
        return identifier
    }

    override fun inSelectionHotspot(e: MotionEvent): Boolean {
        return true
    }

    override fun inDragRegion(e: MotionEvent): Boolean {
        return true
    }
}

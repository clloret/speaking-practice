package com.clloret.speakingpractice.exercise.practice

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class DepthTransformation : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> {
                page.alpha = 0f
            }
            position <= 0 -> {
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            }
            position <= 1 -> {
                page.translationX = -position * page.width
                page.alpha = 1 - abs(position)
                page.scaleX = 1 - abs(position)
                page.scaleY = 1 - abs(position)
            }
            else -> {
                page.alpha = 0f
            }
        }
    }
}

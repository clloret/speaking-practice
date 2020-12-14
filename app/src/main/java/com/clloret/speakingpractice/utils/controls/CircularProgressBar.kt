package com.clloret.speakingpractice.utils.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.CircularProgressBarViewBinding

class CircularProgressBar : ConstraintLayout {

    private var ui: CircularProgressBarViewBinding =
        CircularProgressBarViewBinding.inflate(
            LayoutInflater.from(context), this, true
        )

    var progress: Int = 0
        set(value) {
            ui.progressBar.progress = value
            field = value
        }

    var max: Int = 0
        set(value) {
            ui.progressBar.max = value
            field = value
        }

    var value: String? = null
        set(value) {
            ui.textView.text = value
            field = value
        }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setStyledAttributes(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        setStyledAttributes(context, attrs)
    }

    private fun setStyledAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar).apply {
            max = getInt(R.styleable.CircularProgressBar_max, max)
            progress = getInt(R.styleable.CircularProgressBar_progress, progress)
            value = getString(R.styleable.CircularProgressBar_value)
            recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        ui.progressBar.max = max
        ui.progressBar.progress = progress
        ui.textView.text = value
    }
}

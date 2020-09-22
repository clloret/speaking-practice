package com.clloret.speakingpractice.utils.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.clloret.speakingpractice.R
import kotlinx.android.synthetic.main.circular_progress_bar_view.view.*

class CircularProgressBar : ConstraintLayout {

    var progress: Int = 0
        set(value) {
            progressBar?.progress = value
            field = value
        }

    var max: Int = 0
        set(value) {
            progressBar?.max = value
            field = value
        }

    var value: String? = null
        set(value) {
            textView?.text = value
            field = value
        }

    constructor(context: Context) : super(context) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setStyledAttributes(context, attrs)
        initializeViews(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        setStyledAttributes(context, attrs)
        initializeViews(context)
    }

    private fun setStyledAttributes(context: Context, attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.CircularProgressBar).apply {
            max = getInt(R.styleable.CircularProgressBar_max, max)
            progress = getInt(R.styleable.CircularProgressBar_progress, progress)
            value = getString(R.styleable.CircularProgressBar_value)
            recycle()
        }
    }

    private fun initializeViews(context: Context) {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.circular_progress_bar_view, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        progressBar.max = max
        progressBar.progress = progress
        textView.text = value
    }
}

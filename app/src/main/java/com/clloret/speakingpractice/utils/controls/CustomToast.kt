package com.clloret.speakingpractice.utils.controls

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.clloret.speakingpractice.R

class CustomToast {
    companion object {
        @SuppressLint("InflateParams")
        fun makeText(
            context: Context,
            text: CharSequence,
            @DrawableRes backgroundResId: Int,
            @DrawableRes imageResId: Int,
            duration: Int
        ): Toast {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout: View = layoutInflater.inflate(R.layout.custom_toast, null)
            layout.setBackgroundResource(backgroundResId)

            val textView = layout.findViewById<TextView>(R.id.text)
            textView.text = text
            textView.setCompoundDrawablesWithIntrinsicBounds(imageResId, 0, 0, 0)

            val toast = Toast(context)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.duration = duration
            toast.view = layout
            return toast
        }

        fun makeText(
            context: Context,
            @StringRes textResId: Int,
            @DrawableRes backgroundResId: Int,
            @DrawableRes imageResId: Int,
            duration: Int
        ): Toast {
            return makeText(
                context,
                context.resources.getText(textResId),
                backgroundResId,
                imageResId,
                duration
            )
        }
    }
}
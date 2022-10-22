package com.udacity

import android.graphics.Color
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("downloadStatus")
fun bindStatus(textView: TextView, status: String) {
    when (status) {
        textView.context.getString(R.string.success) -> textView.setTextColor(Color.GREEN)
        else -> textView.setTextColor(Color.RED)
    }
}

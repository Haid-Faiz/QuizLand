package com.example.quiz_app_mvvm.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackBar(
    message: String,
    action: (() -> Unit)? = null,
    actionMsg: String? = null
) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).apply {
        setAction(
            actionMsg,
            View.OnClickListener {
                action?.invoke()
            }
        )
        show()
    }
}

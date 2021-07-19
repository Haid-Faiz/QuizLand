package com.example.quiz_app_mvvm.util

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackBar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

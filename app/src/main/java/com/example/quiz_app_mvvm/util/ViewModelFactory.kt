package com.example.quiz_app_mvvm.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quiz_app_mvvm.ui.auth.AuthViewModel


class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return super.create(modelClass)
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel() as T
            else -> throw IllegalArgumentException("Unknown viewModel class")
        }
    }
}
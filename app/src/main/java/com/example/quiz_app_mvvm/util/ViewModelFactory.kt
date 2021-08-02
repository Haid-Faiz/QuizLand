package com.example.quiz_app_mvvm.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quiz_app_mvvm.repositories.QuizRepo
import com.example.quiz_app_mvvm.repositories.UserRepo
import com.example.quiz_app_mvvm.ui.auth.AuthViewModel
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel

class ViewModelFactory constructor(
    private val userRepo: UserRepo,
    private val quizRepo: QuizRepo
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return super.create(modelClass)
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(userRepo) as T
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> QuizViewModel(quizRepo) as T
            else -> throw IllegalArgumentException("Unknown viewModel class")
        }
    }
}

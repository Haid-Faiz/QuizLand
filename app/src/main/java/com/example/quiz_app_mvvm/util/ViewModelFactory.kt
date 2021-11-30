// package com.example.quiz_app_mvvm.util
//
// class ViewModelFactory constructor(
//    private val userRepo: UserRepo,
//    private val quizRepo: QuizRepo
// ) : ViewModelProvider.NewInstanceFactory() {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
// //        return super.create(modelClass)
//        return when {
//            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(userRepo) as T
//            modelClass.isAssignableFrom(QuizViewModel::class.java) -> QuizViewModel(quizRepo) as T
//            else -> throw IllegalArgumentException("Unknown viewModel class")
//        }
//    }
// }

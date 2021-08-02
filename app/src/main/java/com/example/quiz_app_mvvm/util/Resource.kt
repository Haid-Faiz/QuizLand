package com.example.quiz_app_mvvm.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data = data)
    class Error<T>(message: String) : Resource<T>(message = message)
    class Loading<T>(data: T? = null) : Resource<T>(data) // here i passed data in loading state also
    // because sometimes we may need to show
    // some data during loading state
}

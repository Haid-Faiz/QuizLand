package com.example.quiz_app_mvvm.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quiz_app_mvvm.repositories.UserRepo
import com.example.quiz_app_mvvm.util.Resource
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch


class AuthViewModel() : ViewModel() {


    private val userRepo: UserRepo = UserRepo()

    private var _userAuthResult: MutableLiveData<Resource<AuthResult>> = MutableLiveData()
    val userAuthResult = _userAuthResult

    fun signInWithGoogleAuthCredential(
        googleAuthCredential: AuthCredential
    ) = viewModelScope.launch {
        _userAuthResult.postValue(Resource.Loading())
        _userAuthResult.postValue(userRepo.firebaseSignInWithGoogle(googleAuthCredential))
    }
}
package com.example.quiz_app_mvvm.repositories

import com.example.quiz_app_mvvm.model.User
import com.example.quiz_app_mvvm.util.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class UserRepo : BaseRepo() {
    val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("Users")

    suspend fun addUser(user: User): Resource<Void> = safeApiCall {
        collection.document(user.userID).set(user)
    }

    //--------------------------------------------------------------

    suspend fun firebaseSignInWithGoogle(authCredential: AuthCredential): Resource<AuthResult> {
        return safeApiCall {
            val task: Task<AuthResult> = Firebase.auth.signInWithCredential(authCredential)
            task
        }
    }
}
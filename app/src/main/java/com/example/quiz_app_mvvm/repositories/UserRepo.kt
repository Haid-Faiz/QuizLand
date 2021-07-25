package com.example.quiz_app_mvvm.repositories

import android.content.Context
import com.example.quiz_app_mvvm.model.User
import com.example.quiz_app_mvvm.util.Constants.USERS_COLLECTION
import com.example.quiz_app_mvvm.util.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val user: FirebaseUser?,
    private val db: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : BaseRepo(context) {

    private val collection = db.collection(USERS_COLLECTION)

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
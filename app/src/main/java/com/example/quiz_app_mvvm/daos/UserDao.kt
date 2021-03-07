package com.example.quiz_app_mvvm.daos

import com.example.quiz_app_mvvm.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("Users")

    fun addUser(user: User?){
        GlobalScope.launch(Dispatchers.IO) {
            user?.let {
                collection.document(user.userID).set(it)
            }
        }
    }
}
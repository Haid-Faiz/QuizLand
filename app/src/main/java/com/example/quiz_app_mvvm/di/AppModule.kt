package com.example.quiz_app_mvvm.di

import com.example.quiz_app_mvvm.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun providesUser(): User {
        return User(
            userID = Firebase.auth.currentUser?.uid!!,
            displayName = Firebase.auth.currentUser?.displayName,
            userImageUrl = Firebase.auth.currentUser?.photoUrl.toString()
        )
    }

    @Provides
    @Singleton
    fun providesFirebaseUser(): FirebaseUser? = Firebase.auth.currentUser

    @Provides
    @Singleton
    fun providesFirebaseDb(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesStorageRef(): StorageReference = FirebaseStorage.getInstance().reference
}
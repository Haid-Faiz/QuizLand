package com.example.quiz_app_mvvm.repositories

import android.util.Log
import com.example.quiz_app_mvvm.util.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BaseRepo {

    suspend fun <T> safeApiCall(api: () -> Task<T>): Resource<T> {
        return withContext(Dispatchers.IO) {
            // now withContext will automatically return the result
            try {
                val task: Task<T> = api()
                val result: T = task.await()
                Resource.Success(data = result)
            } catch (e: FirebaseException) {
                Log.d("check1", "safeApiCall: ${e.message}")
                Resource.Error(message = e.message!!)
            }
        }
    }
}
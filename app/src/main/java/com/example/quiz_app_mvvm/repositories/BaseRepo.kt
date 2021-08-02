package com.example.quiz_app_mvvm.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.quiz_app_mvvm.util.Resource
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

abstract class BaseRepo(private val context: Context) {

    suspend fun <T> safeApiCall(api: () -> Task<T>): Resource<T> {
        return withContext(Dispatchers.IO) {
            // now withContext will automatically return the result
            if (isInternetAvailable()) {
                try {
                    val task: Task<T> = api()
                    val result: T = task.await()
                    Log.d("success_status", "safeApiCall: ${task.isSuccessful}")
                    Resource.Success(data = result)
                } catch (e: FirebaseNetworkException) {
                    Resource.Error(message = e.message ?: "Something went wrong with network")
                } catch (e: FirebaseException) {
                    Log.d("checkError", "safeApiCall: ${e.message}")
                    Resource.Error(message = e.message ?: "Something went wrong")
                }
            } else Resource.Error(message = "Please check your internet connection")
        }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false

        return when {
            nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    }
}

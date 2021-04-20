package com.example.quiz_app_mvvm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class QuizBroadcastReceiver : BroadcastReceiver() {

    private var isConnected = false

    override fun onReceive(context: Context?, receivedIntent: Intent?) { // this context is the context of our activity

        if (receivedIntent == null)
            return

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        isConnected = if (networkInfo != null && networkInfo.isConnected)
            networkInfo.type == ConnectivityManager.TYPE_WIFI || networkInfo.type == ConnectivityManager.TYPE_MOBILE
        else false

        val connectivityListener = context as ConnectivityListener
        connectivityListener.onConnectionReceive(isConnected)
    }

    interface ConnectivityListener {
        fun onConnectionReceive(isConnected: Boolean)
    }
}
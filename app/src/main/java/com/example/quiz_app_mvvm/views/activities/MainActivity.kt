package com.example.quiz_app_mvvm.views.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.broadcast.QuizBroadcastReceiver
import com.example.quiz_app_mvvm.databinding.ActivityMainBinding
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.example.quiz_app_mvvm.views.fragments.ListFragment
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity(), QuizBroadcastReceiver.ConnectivityListener {

    private lateinit var broadcastReceiver: QuizBroadcastReceiver
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)
        broadcastReceiver = QuizBroadcastReceiver()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        // we can only use this line if we added layout tag in its xml
//        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
        DialogsUtil.dismissDialog()
    }

    override fun onConnectionReceive(isConnected: Boolean) {
        if (navController.currentDestination?.id != R.id.startFragment) {
            if (!isConnected)
                DialogsUtil.showConnectionErrorDialog(this@MainActivity, isConnected)
            else
                DialogsUtil.dismissDialog()
        }
    }
}
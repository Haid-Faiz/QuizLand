package com.example.quiz_app_mvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.broadcast.QuizBroadcastReceiver
import com.example.quiz_app_mvvm.databinding.ActivityMainBinding
import com.example.quiz_app_mvvm.ui.fragments.ListFragmentDirections
import com.example.quiz_app_mvvm.util.DialogsUtil

class MainActivity : AppCompatActivity(),
    ClickListeners {     // , QuizBroadcastReceiver.ConnectivityListener

    private lateinit var broadcastReceiver: QuizBroadcastReceiver
    private lateinit var navController: NavController
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
//        broadcastReceiver = QuizBroadcastReceiver()
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        // we can only use this line if we added layout tag in its xml
        // DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
//        unregisterReceiver(broadcastReceiver)
        DialogsUtil.dismissDialog()
    }

//    override fun onConnectionReceive(isConnected: Boolean) {
//        if (navController.currentDestination?.id != R.id.startFragment) {
//            if (!isConnected)
//                DialogsUtil.showConnectionErrorDialog(this@MainActivity, isConnected)
//            else
//                DialogsUtil.dismissDialog()
//        }
//    }

    override fun oViewQuizClicked(position: Int) {
        val action = ListFragmentDirections.actionListFragmentToDetailsFragment()
        action.position = position
        navController.navigate(action)
    }

    override fun gotoAuthFragment() {
        if (navController.currentDestination?.id == R.id.listFragment) {
            navController.navigate(R.id.action_listFragment_to_startFragment)
        }
    }

}

interface ClickListeners {
    fun oViewQuizClicked(position: Int)
    fun gotoAuthFragment()
}

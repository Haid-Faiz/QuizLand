package com.example.quiz_app_mvvm.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ClickListeners {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        // we can only use this line if we added layout tag in its xml
        // DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

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

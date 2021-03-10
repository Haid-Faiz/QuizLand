package com.example.quiz_app_mvvm.views.fragments

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.broadcast.QuizBroadcastReceiver
import com.example.quiz_app_mvvm.databinding.FragmentListBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListFragment : Fragment(){

    private lateinit var navController: NavController
    private lateinit var fragmentListBinding: FragmentListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false)
        return fragmentListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        quizBroadcastReceiver = QuizBroadcastReceiver()
        navController = Navigation.findNavController(requireActivity(), R.id.list_fragment_host)
        navControllerMain = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
        NavigationUI.setupWithNavController(fragmentListBinding.bottomNavBar, navController)
    }

    companion object {
        lateinit var navControllerMain: NavController

        //    @Override
        //    public void onQuizItemClicked(int position) {
        //
        //        ListFragmentDirections.ActionListFragmentToDetailsFragment action =
        //                ListFragmentDirections.actionListFragmentToDetailsFragment();
        //
        //        action.setPosition(position);
        //        navControllerTwo.navigate(action);
        //    }
        @JvmStatic
        fun helloCheck(position: Int) {
            val action = ListFragmentDirections.actionListFragmentToDetailsFragment()
            action.position = position
            navControllerMain.navigate(action)
        }

        @JvmStatic
        fun goToStartFrag(){
            if (navControllerMain.currentDestination?.id == R.id.listFragment){
                navControllerMain.navigate(R.id.action_listFragment_to_startFragment)
            }
        }
    }
}
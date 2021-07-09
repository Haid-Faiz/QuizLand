package com.example.quiz_app_mvvm.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var _binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            navController = findNavController(it, R.id.ls_frag_host)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            _binding.bottomNavBar.isVisible =
                !(destination.id == R.id.addQuestFragment || destination.id == R.id.createQuizFragment)
        }
        _binding.bottomNavBar.setupWithNavController(navController)
    }
}
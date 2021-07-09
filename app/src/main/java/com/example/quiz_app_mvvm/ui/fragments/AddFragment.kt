package com.example.quiz_app_mvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentAddBinding
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddFragment : BottomSheetDialogFragment() {

    private lateinit var navController: NavController
    private lateinit var fragmentAddBinding: FragmentAddBinding
    private lateinit var viewModel: QuizListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentAddBinding = FragmentAddBinding.inflate(inflater, container, false)
        return fragmentAddBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.ls_frag_host)
        viewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)

        fragmentAddBinding.createQuizSelectBtn.setOnClickListener {
            navController.navigate(R.id.action_addFragment_to_createQuizFragment)
        }

        fragmentAddBinding.joinQuizSelectBtn.setOnClickListener {
            navController.navigate(R.id.action_addFragment_to_joinQuizBSDFragment)
            navController.popBackStack()
        }
    }
}
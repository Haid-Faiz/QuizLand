package com.example.quiz_app_mvvm.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : BottomSheetDialogFragment() {

    private lateinit var navController: NavController
    private lateinit var _binding: FragmentAddBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.ls_frag_host)

        _binding.createQuizSelectBtn.setOnClickListener {
            navController.navigate(R.id.action_addFragment_to_createQuizFragment)
        }

        _binding.joinQuizSelectBtn.setOnClickListener {
            navController.navigate(R.id.action_addFragment_to_joinQuizBSDFragment)
            navController.popBackStack()
        }
    }
}

package com.example.quiz_app_mvvm.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentAccountBinding
import com.example.quiz_app_mvvm.model.User
import com.example.quiz_app_mvvm.util.Constants.LINKED_ANDROID_APP_PACKAGE
import com.example.quiz_app_mvvm.util.Constants.MY_LINKED_PROFILE
import com.example.quiz_app_mvvm.util.Constants.QUIZ_LAND_SHARE_MSG
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private lateinit var _binding: FragmentAccountBinding
    private lateinit var navController: NavController
    private lateinit var clickListener: ClickListeners

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickListener = context as ClickListeners
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.ls_frag_host)

        Firebase.auth.currentUser?.let {
            val user = User(it.uid, it.displayName, it.photoUrl.toString())
            _binding.user = user
        }

        _binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            navController.popBackStack()
            clickListener.gotoAuthFragment()
        }

        _binding.shareAppButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                QUIZ_LAND_SHARE_MSG
            )
            val chooser = Intent.createChooser(shareIntent, "Share this app using...")
            startActivity(chooser)
        }

        _binding.myCreatedQuizButton.setOnClickListener {
            navController.navigate(R.id.action_accountFragment_to_createdQuizesFragment)
        }

        _binding.myResultsButton.setOnClickListener {
            navController.navigate(R.id.action_accountFragment_to_myResultsFragment)
        }

        _binding.developerContactButton.setOnClickListener {
            // This code opens Linked In but doesn't open particular profile
            // val launchLinkedIntent = requireActivity().packageManager.getLaunchIntentForPackage("com.linkedin.android")
            // startActivity(launchLinkedIntent)
            // ---------------------------------------------
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MY_LINKED_PROFILE))
            intent.setPackage(LINKED_ANDROID_APP_PACKAGE)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
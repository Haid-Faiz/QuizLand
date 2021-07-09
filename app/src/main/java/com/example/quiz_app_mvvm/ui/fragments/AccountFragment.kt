package com.example.quiz_app_mvvm.ui.fragments

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
import com.example.quiz_app_mvvm.ui.activities.HelloCheck
import com.example.quiz_app_mvvm.ui.activities.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private lateinit var fragmentAccountBinding: FragmentAccountBinding
    private lateinit var navController: NavController
    private lateinit var a: HelloCheck

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false)
        return fragmentAccountBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        a = context as HelloCheck
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireActivity(), R.id.ls_frag_host)

        Firebase.auth.currentUser?.let {
            val user = User(it.uid, it.displayName, it.photoUrl.toString())
            fragmentAccountBinding.user = user
        }

        fragmentAccountBinding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            navController.popBackStack()
//            ListFragment.goToStartFrag()
            a.gotoStart()
        }

        fragmentAccountBinding.shareAppButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Hey, Do you want to get rid of all quiz headaches ? \n If yes.. then QuizLand is the solution. \n Download now: AppUrl"
            )
            val chooser = Intent.createChooser(shareIntent, "Share this app using...")
            startActivity(chooser)
        }

        fragmentAccountBinding.myCreatedQuizButton.setOnClickListener {
            navController.navigate(R.id.action_accountFragment_to_createdQuizesFragment)
        }

        fragmentAccountBinding.myResultsButton.setOnClickListener {
            navController.navigate(R.id.action_accountFragment_to_myResultsFragment)
        }

        fragmentAccountBinding.developerContactButton.setOnClickListener {
            // Open Linked
            // --------------------------------------------
            // this cade opens Linked In but doesn't open particular profile
//            val launchLinkedIntent = requireActivity().packageManager.getLaunchIntentForPackage("com.linkedin.android")
//            startActivity(launchLinkedIntent)
            // ---------------------------------------------
            val profileID = "https://www.linkedin.com/in/faizan-haider-3a4220193"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(profileID))
            intent.setPackage("com.linkedin.android")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
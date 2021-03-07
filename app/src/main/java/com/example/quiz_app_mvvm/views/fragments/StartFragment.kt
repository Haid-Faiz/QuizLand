package com.example.quiz_app_mvvm.views.fragments

import android.content.Intent
import androidx.navigation.NavController
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.quiz_app_mvvm.R
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.adapter.IntroViewPagerAdapter
import com.example.quiz_app_mvvm.daos.UserDao
import com.example.quiz_app_mvvm.databinding.FragmentStartBinding
import com.example.quiz_app_mvvm.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class StartFragment : Fragment(), FirebaseAuth.AuthStateListener {

    private val GOOGLE_SIGNIN_REQ_CODE: Int = 123
    private lateinit var timer: Timer

    // Navigation JetPack
    private lateinit var navController: NavController
    private lateinit var fragmentStartBinding: FragmentStartBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentStartBinding = FragmentStartBinding.inflate(inflater, container, false)
        return fragmentStartBinding.root
//        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        // Setting up viewpager
        fragmentStartBinding.startViewpager.adapter = IntroViewPagerAdapter(requireActivity())
        fragmentStartBinding.wormDotsIndicator.setViewPager2(fragmentStartBinding.startViewpager)
        handleViewPagerAutoSlide()

        val user = Firebase.auth.currentUser
        if (user != null)
            updateUI(user)

//        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container)
        val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        fragmentStartBinding.googleSigninBtn.setOnClickListener {
            googleSignIn()
        }
    }

    private fun handleViewPagerAutoSlide() {
        timer = Timer()  // This will create a new Thread
        val handler = Handler()
        timer.schedule(object : TimerTask() {
            override fun run() {

                handler.post(object : Runnable {
                    override fun run() {
                        var c = fragmentStartBinding.startViewpager.currentItem
                        if (c == 2) fragmentStartBinding.startViewpager.currentItem = 0 else fragmentStartBinding.startViewpager.currentItem = ++c
                    }
                })
            }
        }, 2400, 2400)
    }

    private fun googleSignIn() {
        // clearing previous signin caches
        googleSignInClient.signOut()
        // getting sign in intent
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, GOOGLE_SIGNIN_REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGNIN_REQ_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {

        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)

        fragmentStartBinding.startProgressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {

            Firebase.auth.signInWithCredential(authCredential).await()
            val user: FirebaseUser? = Firebase.auth.currentUser
            // we can update UI from UI thread & can't update UI from
            // background thread otherwise it will generate some errors
            withContext(Dispatchers.Main) {
                updateUI(user)
            }
        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            // send user data to firestore collection
            val user = User(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl.toString())
            val userDao = UserDao()
            userDao.addUser(user)

            // This is important.... Ha... Ha... Ha... Ha....
            if (navController.currentDestination?.id == R.id.startFragment) {
                navController.navigate(R.id.action_startFragment_to_listFragment)
            }
        } else {
            fragmentStartBinding.startProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        Firebase.auth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        Firebase.auth.removeAuthStateListener(this)
        timer.cancel()
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        updateUI(firebaseAuth.currentUser)
    }
}
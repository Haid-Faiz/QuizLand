package com.example.quiz_app_mvvm.ui.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.navigation.NavController
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.quiz_app_mvvm.R
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.databinding.FragmentAuthBinding
import com.example.quiz_app_mvvm.model.User
import com.example.quiz_app_mvvm.util.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class AuthFragment : Fragment(), FirebaseAuth.AuthStateListener {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var timer: Timer
    private lateinit var navController: NavController
    private lateinit var _binding: FragmentAuthBinding
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val resultantIntent = result.data
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(resultantIntent)
                    try {
                        val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                        account?.let {
                            getGoogleAuthCredential(account)
                        }
                    } catch (e: ApiException) {
                        Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setUpViewPager()

        val user = Firebase.auth.currentUser
        user?.let { updateUI(user) }

        // Building GSO
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        // getting sign in client
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        _binding.googleSigninBtn.setOnClickListener {
            googleSignIn(googleSignInClient)
        }
    }

    private fun googleSignIn(googleSignInClient: GoogleSignInClient) {
        // clearing previous sign in caches
        googleSignInClient.signOut()
        // getting sign in intent
        val intent = googleSignInClient.signInIntent
        startForResult.launch(intent)
    }

    private fun getGoogleAuthCredential(googleSignInAccount: GoogleSignInAccount?) {
        val tokenId = googleSignInAccount?.idToken
        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(tokenId, null)
        // signing with googleAuthCredential
        authViewModel.signInWithGoogleAuthCredential(authCredential)

        authViewModel.userAuthResult.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> updateUI(
                    it.data!!.user,
                    it.data.additionalUserInfo!!.isNewUser
                )
                is Resource.Error -> {
                    _binding.startProgressBar.isVisible = false
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> _binding.startProgressBar.isVisible = true
            }
        }

//        lifecycleScope.launch(Dispatchers.IO) {
//            Firebase.auth.signInWithCredential(authCredential).await()
//            val user: FirebaseUser? = Firebase.auth.currentUser
//            // we can update UI from UI thread & can't update UI from
//            // background thread otherwise it will generate some errors
//            withContext(Dispatchers.Main) {
//                updateUI(user)
//            }
//        }
    }

    private fun handleViewPagerAutoSlide() {
        timer = Timer()  // This will create a new Thread
        val handler = Handler(Looper.getMainLooper())

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Runnable {
                    var c = _binding.startViewpager.currentItem
                    if (c == 2) _binding.startViewpager.currentItem = 0
                    else _binding.startViewpager.currentItem = ++c
                })
            }
        }, 3000, 3000)
    }

    private fun updateUI(firebaseUser: FirebaseUser?, isNewUser: Boolean = false) {
        firebaseUser?.let {
            if (isNewUser) {
                // save this new user to database
                val user = User(
                    firebaseUser.uid,
                    firebaseUser.displayName,
                    firebaseUser.photoUrl.toString()
                )
                authViewModel.addUser(user)
            }
            if (navController.currentDestination?.id == R.id.startFragment)
                navController.navigate(R.id.action_startFragment_to_listFragment)
        }
    }

    private fun setUpViewPager() {
        _binding.startViewpager.adapter = IntroPagerAdapter(requireContext())
        _binding.wormDotsIndicator.setViewPager2(_binding.startViewpager)
        handleViewPagerAutoSlide()
    }

    override fun onStart() {
        super.onStart()
        Firebase.auth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        Firebase.auth.removeAuthStateListener(this)
    }

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
        updateUI(firebaseAuth.currentUser)
    }
}
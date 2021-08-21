package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.databinding.FragmentAdminResultBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminResultFragment : Fragment() {

    private lateinit var navController: NavController
    private val quizViewModel: QuizViewModel by activityViewModels()
    private var _binding: FragmentAdminResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var receivedQuizData: QuizModel
    private var publicResultsAdapter: PublicResultsAdapter? = null
    private lateinit var arr: ObservableSnapshotArray<MyResult>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        receivedQuizData = quizViewModel.getQuizData()
        binding.apply {
            quizData = receivedQuizData
            adminResultDiscardBtn.setOnClickListener { navController.popBackStack() }
            rankListRecyclerview.setHasFixedSize(true)
            retryButton.setOnClickListener {
                quizViewModel.getPublicResults(quizID = receivedQuizData.quiz_id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        quizViewModel.getPublicResults(quizID = receivedQuizData.quiz_id)
        quizViewModel.resultList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    binding.publicResultsProgressBar.isVisible = false
                    binding.rankListRecyclerview.isVisible = false
                    showSnackBar(message = it.message!!)
                    binding.statusBox.isVisible = true
                    binding.noOneParticipatedText.isVisible = false
                }
                is Resource.Loading -> {
                    binding.publicResultsProgressBar.isVisible = true
                    binding.rankListRecyclerview.isVisible = false
                    binding.statusBox.isVisible = false
                    binding.noOneParticipatedText.isVisible = false
                }
                is Resource.Success -> {
                    binding.publicResultsProgressBar.isVisible = false
                    binding.statusBox.isVisible = false
                    Log.d("listSize", "onStart: ${it.data?.size()}")
                    if (it.data!!.isEmpty) {
                        binding.rankListRecyclerview.isVisible = false
                        binding.noOneParticipatedText.isVisible = true
                    } else {
                        binding.rankListRecyclerview.isVisible = true
                        binding.noOneParticipatedText.isVisible = false
                        binding.adminResultTotalParticipants.text = it.data.size().toString()

                        val options = FirestoreRecyclerOptions.Builder<MyResult>()
                            .setQuery(it.data.query, MyResult::class.java)
                            .build()

                        arr = options.snapshots
                        publicResultsAdapter = PublicResultsAdapter(
                            options = options,
                            clickListenerFunction = { myResult: MyResult ->
                                DialogsUtil.showParticipantDetailResult(requireContext(), myResult)
                            }
                        )
                        publicResultsAdapter?.startListening()
                        binding.rankListRecyclerview.adapter = publicResultsAdapter
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        publicResultsAdapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

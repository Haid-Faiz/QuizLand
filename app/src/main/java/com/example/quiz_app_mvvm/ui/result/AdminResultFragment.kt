package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.databinding.FragmentAdminResultBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminResultFragment : Fragment() {

    private lateinit var navController: NavController
    private val quizViewModel: QuizViewModel by activityViewModels()
    private lateinit var _binding: FragmentAdminResultBinding
    private lateinit var quizData: QuizModel
    private lateinit var publicResultsAdapter: PublicResultsAdapter
    private lateinit var arr: ObservableSnapshotArray<MyResult>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdminResultBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        quizData = quizViewModel.getQuizData()
        _binding.quizData = quizData
        _binding.adminResultDiscardBtn.setOnClickListener { navController.popBackStack() }
        _binding.rankListRecyclerview.setHasFixedSize(true)
        _binding.retryButton.setOnClickListener {
            quizViewModel.getPublicResults(quizID = quizData.quiz_id)
        }
    }

    override fun onStart() {
        super.onStart()
        quizViewModel.getPublicResults(quizID = quizData.quiz_id)
        quizViewModel.resultList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    _binding.publicResultsProgressBar.isVisible = false
                    _binding.rankListRecyclerview.isVisible = false
                    showSnackBar(message = it.message!!)
                    _binding.statusBox.isVisible = true
                }
                is Resource.Loading -> {
                    _binding.publicResultsProgressBar.isVisible = true
                    _binding.rankListRecyclerview.isVisible = false
                    _binding.statusBox.isVisible = false
                }
                is Resource.Success -> {
                    _binding.publicResultsProgressBar.isVisible = false
                    _binding.rankListRecyclerview.isVisible = true
                    _binding.statusBox.isVisible = false
                    val options = FirestoreRecyclerOptions.Builder<MyResult>()
                        .setQuery(it.data?.query!!, MyResult::class.java)
                        .build()

                    arr = options.snapshots
                    publicResultsAdapter = PublicResultsAdapter(options = options,
                        clickListenerFunction = { myResult: MyResult ->
                            DialogsUtil.showParticipantDetailResult(requireContext(), myResult)
                        },
                        onItemChanged = { itemCount: Int ->
                            _binding.adminResultTotalParticipants.text =
                                itemCount.toString()
                            onListItemChanged(itemCount)
                        })
                    publicResultsAdapter.startListening()
                    _binding.rankListRecyclerview.adapter = publicResultsAdapter
                }
            }
        }
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            _binding.noOneParticipatedText.isVisible = true
            _binding.rankListRecyclerview.isVisible = false
        } else _binding.noOneParticipatedText.isVisible = false
    }

    override fun onStop() {
        super.onStop()
        publicResultsAdapter?.stopListening()
    }
}
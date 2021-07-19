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
        _binding.adminResultDiscardBtn.setOnClickListener {
            navController.popBackStack()
        }

        _binding.participantsRankListRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())
        _binding.participantsRankListRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        quizViewModel.getPublicResults(quizID = quizData.quiz_id)
        quizViewModel.resultList.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> showSnackBar(message = it.message ?: "Something went wrong")
                is Resource.Loading -> {
                    _binding.publicResultsProgressBar.isVisible = true
                    _binding.participantsRankListRecyclerview.isVisible = false
                }
                is Resource.Success -> {
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
                    _binding.participantsRankListRecyclerview.adapter =
                        publicResultsAdapter
                }
            }
        }
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            _binding.noOneParticipatedText.visibility = View.VISIBLE
            _binding.participantsRankListRecyclerview.visibility = View.INVISIBLE
        } else {
            _binding.noOneParticipatedText.visibility = View.INVISIBLE
            _binding.participantsRankListRecyclerview.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        publicResultsAdapter?.stopListening()
    }
}
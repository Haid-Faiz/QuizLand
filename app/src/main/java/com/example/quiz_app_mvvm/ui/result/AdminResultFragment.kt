package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.databinding.FragmentAdminResultBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.ui.quiz.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray


class AdminResultFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var quizListViewModel: QuizListViewModel
    private lateinit var fragmentAdminResultBinding: FragmentAdminResultBinding
    private lateinit var quizData: QuizModel
    private lateinit var publicResultsAdapter: PublicResultsAdapter
    private lateinit var arr: ObservableSnapshotArray<MyResult>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentAdminResultBinding = FragmentAdminResultBinding.inflate(inflater, container, false)
        return fragmentAdminResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quizData = quizListViewModel.getQuizData()
        fragmentAdminResultBinding.quizData = quizData
        fragmentAdminResultBinding.adminResultDiscardBtn.setOnClickListener {
            navController.popBackStack()
        }

        fragmentAdminResultBinding.participantsRankListRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        fragmentAdminResultBinding.participantsRankListRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        quizListViewModel.getPublicResults(quizID = quizData.quiz_id)
        quizListViewModel.publicResultOptions.observe(viewLifecycleOwner, {

            arr = it.snapshots
            publicResultsAdapter = PublicResultsAdapter(options = it,
                    clickListenerFunction = { myResult: MyResult ->
                        DialogsUtil.showParticipantDetailResult(requireContext(), myResult)
                    },
                    onItemChanged = { itemCount: Int ->
                        fragmentAdminResultBinding.adminResultTotalParticipants.text = itemCount.toString()
                        onListItemChanged(itemCount)
                    })
            publicResultsAdapter.startListening()
            fragmentAdminResultBinding.participantsRankListRecyclerview.adapter = publicResultsAdapter

            fragmentAdminResultBinding.publicResultsProgressBar.visibility = View.INVISIBLE
        })
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            fragmentAdminResultBinding.noOneParticipatedText.visibility = View.VISIBLE
            fragmentAdminResultBinding.participantsRankListRecyclerview.visibility = View.INVISIBLE
        } else {
            fragmentAdminResultBinding.noOneParticipatedText.visibility = View.INVISIBLE
            fragmentAdminResultBinding.participantsRankListRecyclerview.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        publicResultsAdapter?.stopListening()
    }
}
package com.example.quiz_app_mvvm.ui.quiz.join

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentJoinedQuizzesBinding
import com.example.quiz_app_mvvm.databinding.TransparentDialogBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.ClickListeners
import com.example.quiz_app_mvvm.ui.quiz.QuizListAdapter
import com.example.quiz_app_mvvm.ui.quiz.QuizListAdapter.OnQuizListItemClicked
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinedQuizzesFragment : Fragment(), OnQuizListItemClicked {

    private val quizViewModel: QuizViewModel by activityViewModels()
    private var quizListAdapter: QuizListAdapter? = null
    private lateinit var _binding: FragmentJoinedQuizzesBinding
    private lateinit var arr: ObservableSnapshotArray<QuizModel>
    private lateinit var clickListeners: ClickListeners

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentJoinedQuizzesBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.joinedPageRecyclerview.setHasFixedSize(true)
        _binding.retryButton.setOnClickListener { quizViewModel.getParticipatedQuizList() }
    }

    override fun onStart() {
        super.onStart()
        quizViewModel.getParticipatedQuizList()
        quizViewModel.quizList.observe(
            viewLifecycleOwner,
            {

                when (it) {
                    is Resource.Error -> {
                        showSnackBar(message = it.message!!)
                        _binding.joinedPageProgressBar.isVisible = false
                        _binding.joinedPageRecyclerview.isVisible = false
                        _binding.statusBox.isVisible = true
                    }
                    is Resource.Loading -> {
                        _binding.joinedPageProgressBar.isVisible = true
                        _binding.joinedPageRecyclerview.isVisible = false
                        _binding.statusBox.isVisible = false
                    }
                    is Resource.Success -> {
                        _binding.joinedPageProgressBar.isVisible = false
                        _binding.joinedPageRecyclerview.isVisible = true
                        _binding.statusBox.isVisible = false
                        val options = FirestoreRecyclerOptions.Builder<QuizModel>()
                            .setQuery(it.data?.query!!, QuizModel::class.java)
                            .build()

                        arr = options.snapshots
                        quizListAdapter = QuizListAdapter(options, this)
                        _binding.joinedPageRecyclerview.adapter = quizListAdapter
                        quizListAdapter?.startListening()
                    }
                }
            }
        )
    }

    override fun onListItemChanged(itemCount: Int) {
        _binding.apply {
            if (itemCount == 0) {
                joinedQuizListEmptyBottle.isVisible = true
                joinedPageRecyclerview.isVisible = false
            } else {
                joinedQuizListEmptyBottle.isVisible = false
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickListeners = context as ClickListeners
    }

    override fun onQuizItemClicked(position: Int) {
        quizViewModel.setQuizData(arr[position])
        clickListeners.oViewQuizClicked(position)
    }

    override fun onUnEnrolClicked(adapterPosition: Int) {
        val dialogView = TransparentDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
            .setView(dialogView.root)
            .setCancelable(false)
            .create()
        dialog.show()
        lifecycleScope.launchWhenCreated {
            quizViewModel.unEnrolQuiz(arr[adapterPosition].quiz_id).let {
                dialog.dismiss()
                when (it) {
                    is Resource.Error -> showSnackBar("Something went wrong")
//                    is Resource.Loading ->  TODO()
                    is Resource.Success -> showSnackBar("Successfully Un-Enrolled")
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        quizListAdapter?.stopListening()
    }
}

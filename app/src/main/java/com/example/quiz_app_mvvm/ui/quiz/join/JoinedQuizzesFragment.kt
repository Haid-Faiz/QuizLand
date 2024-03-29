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
import com.example.quiz_app_mvvm.util.Constants.IS_IT_FIRST_QUIZ
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinedQuizzesFragment : Fragment(), OnQuizListItemClicked {

    private val viewModel: QuizViewModel by activityViewModels()
    private var quizListAdapter: QuizListAdapter? = null
    private var _binding: FragmentJoinedQuizzesBinding? = null
    private val binding get() = _binding!!
    private lateinit var arr: ObservableSnapshotArray<QuizModel>
    private lateinit var clickListeners: ClickListeners

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinedQuizzesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.joinedPageRecyclerview.setHasFixedSize(true)
        binding.retryButton.setOnClickListener { viewModel.getParticipatedQuizList() }

        viewModel.quizList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showSnackBar(message = it.message!!)
                    binding.joinedPageProgressBar.isVisible = false
                    binding.joinedPageRecyclerview.isVisible = false
                    binding.statusBox.isVisible = true
                    binding.emptyListStatus.isVisible = false
                }
                is Resource.Loading -> {
                    binding.joinedPageProgressBar.isVisible = true
                    binding.joinedPageRecyclerview.isVisible = false
                    binding.statusBox.isVisible = false
                    binding.emptyListStatus.isVisible = false
                }
                is Resource.Success -> {
                    binding.joinedPageProgressBar.isVisible = false
                    binding.statusBox.isVisible = false

                    val options = FirestoreRecyclerOptions.Builder<QuizModel>()
                        .setQuery(it.data!!.query, QuizModel::class.java)
                        .build()

                    arr = options.snapshots

                    if (it.data.isEmpty) {
                        binding.joinedPageRecyclerview.isVisible = false
                        binding.emptyListStatus.isVisible = true
                    } else {
                        binding.joinedPageRecyclerview.isVisible = true
                        binding.emptyListStatus.isVisible = false

                        quizListAdapter = QuizListAdapter(options, this)
                        binding.joinedPageRecyclerview.adapter = quizListAdapter
                        quizListAdapter?.startListening()
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            IS_IT_FIRST_QUIZ,
            viewLifecycleOwner
        ) { key, bundle ->
            // Bug Fix: need to reload data when joining first quiz
            if (bundle.getBoolean("isJoined", false) && arr.isEmpty()) {
                viewModel.getParticipatedQuizList()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getParticipatedQuizList()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickListeners = context as ClickListeners
    }

    override fun onQuizItemClicked(position: Int) {
        viewModel.setQuizData(arr[position])
        clickListeners.oViewQuizClicked(position)
    }

    override fun onUnEnrolClicked(adapterPosition: Int) {
        val dialogView = TransparentDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
            .setView(dialogView.root)
            .setCancelable(false)
            .create()
        dialog.show()
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.unEnrolQuiz(arr[adapterPosition].quiz_id).let {
                dialog.dismiss()
                when (it) {
                    is Resource.Error -> showSnackBar("Something went wrong")
                    // is Resource.Loading ->  TODO()
                    is Resource.Success -> {
                        viewModel.getParticipatedQuizList()
                        showSnackBar("Successfully unenrolled")
                        if (quizListAdapter?.getListSize() == 0) {
                            binding.joinedPageRecyclerview.isVisible = false
                            binding.emptyListStatus.isVisible = true
                        } else {
                            binding.joinedPageRecyclerview.isVisible = true
                            binding.emptyListStatus.isVisible = false
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        quizListAdapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

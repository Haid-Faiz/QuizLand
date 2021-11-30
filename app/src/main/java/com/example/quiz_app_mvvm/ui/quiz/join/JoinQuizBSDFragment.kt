package com.example.quiz_app_mvvm.ui.quiz.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.quiz_app_mvvm.databinding.JoinQuizBsdBinding
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.Constants.IS_IT_FIRST_QUIZ
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoinQuizBSDFragment : BottomSheetDialogFragment() {

    private var _binding: JoinQuizBsdBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()
    private var uniqueQuizID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = JoinQuizBsdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cancelButton.setOnClickListener { this.dismiss() }

        binding.joinQuizButton.setOnClickListener {
            uniqueQuizID = binding.enterUniqueQuizId.editText?.text.toString().trim()

            if (uniqueQuizID.isNotEmpty() && uniqueQuizID.isNotBlank()) {
                // show progress
                binding.apply {
                    this@JoinQuizBSDFragment.isCancelable = false
                    enterUniqueQuizId.error = null
                    progressJoining.isVisible = true
                    joinQuizButton.isEnabled = false
                    joinQuizButton.text = ""
                    enterUniqueQuizId.isEnabled = false
                }
                // Check first that if this quiz exists or not
                checkQuizExistence(uniqueQuizID)
            } else binding.enterUniqueQuizId.error = "Required"
        }
    }

    private fun checkQuizExistence(uniqueQuizID: String) {
        lifecycleScope.launchWhenCreated {
            quizViewModel.isQuizExist(quizID = uniqueQuizID).let {
                when (it) {
                    is Resource.Error -> {
                        binding.apply {
                            progressJoining.isVisible = false
                            joinQuizButton.isEnabled = true
                            joinQuizButton.text = "Join quiz"
                            enterUniqueQuizId.isEnabled = true
                            this@JoinQuizBSDFragment.isCancelable = true
                        }
                        showSnackBar(it.message!!)
                    }
                    is Resource.Success -> {
                        if (it.data?.exists()!!) {
                            // Quiz exists... join it
                            joinQuiz(uniqueQuizID)
                        } else {
                            binding.apply {
                                progressJoining.isVisible = false
                                joinQuizButton.isEnabled = true
                                joinQuizButton.text = "Join quiz"
                                enterUniqueQuizId.isEnabled = true
                                this@JoinQuizBSDFragment.isCancelable = true
                            }
                            showSnackBar(message = "Invalid quiz id or this quiz doesn't exist")
                        }
                    }
                }
            }
        }
    }

    private suspend fun joinQuiz(uniqueQuizID: String) {
        quizViewModel.joinQuiz(uniqueQuizID).let { isJoined: Resource<Void> ->
            binding.apply {
                progressJoining.isVisible = false
                joinQuizButton.isEnabled = true
                enterUniqueQuizId.isEnabled = true
                this@JoinQuizBSDFragment.isCancelable = true
            }
            when (isJoined) {
                is Resource.Error -> showSnackBar(message = isJoined.message!!)
                // is Resource.Loading -> TODO()
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Joined successfully", Toast.LENGTH_SHORT)
                        .show()
                    showSnackBar("Joined successfully")
                    parentFragmentManager.setFragmentResult(
                        IS_IT_FIRST_QUIZ,
                        bundleOf("isJoined" to true)
                    )
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

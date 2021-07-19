package com.example.quiz_app_mvvm.ui.quiz.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.quiz_app_mvvm.databinding.JoinQuizBsdBinding
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class JoinQuizBSDFragment : BottomSheetDialogFragment() {

    private lateinit var _binding: JoinQuizBsdBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private var uniqueQuizID = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = JoinQuizBsdBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding.cancelButton.setOnClickListener { this.dismiss() }

        _binding.joinQuizButton.setOnClickListener {
            uniqueQuizID = _binding.enterUniqueQuizId.editText?.text.toString().trim()

            if (uniqueQuizID.isNotEmpty() && uniqueQuizID.isNotBlank()) {
                // show progress
                _binding.apply {
                    this@JoinQuizBSDFragment.isCancelable = false
                    enterUniqueQuizId.error = null
                    progressJoining.isVisible = true
                    joinQuizButton.isEnabled = false
                    joinQuizButton.text = ""
                    enterUniqueQuizId.isEnabled = false
                }
                // Check first that if this quiz exists or not
                checkQuizExistence(uniqueQuizID)
            } else _binding.enterUniqueQuizId.error = "Required"
        }
    }

    private fun checkQuizExistence(uniqueQuizID: String) {
        lifecycleScope.launchWhenCreated {
            quizViewModel.isQuizExist(quizID = uniqueQuizID).let {
                when (it) {
                    // is Resource.Error -> DialogsUtil.dismissDialog()
                    // is Resource.Loading -> DialogsUtil.showLoadingDialog(requireActivity())
                    is Resource.Success -> {
                        if (it.data?.exists()!!) {
                            // Quiz exists... join it
                            joinQuiz(uniqueQuizID)
                        } else {
                            _binding.apply {
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
            _binding.apply {
                progressJoining.isVisible = false
                joinQuizButton.isEnabled = true
                enterUniqueQuizId.isEnabled = true
                this@JoinQuizBSDFragment.isCancelable = true
            }
            when (isJoined) {
                is Resource.Error -> showSnackBar(
                    message = isJoined.message ?: "Something went wrong"
                )
                // is Resource.Loading -> TODO()
                is Resource.Success -> {
                    showSnackBar("Joined successfully")
                    dismiss()
                }
            }
        }
    }
}
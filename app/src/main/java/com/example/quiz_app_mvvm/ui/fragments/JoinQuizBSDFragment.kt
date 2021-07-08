package com.example.quiz_app_mvvm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.quiz_app_mvvm.databinding.JoinQuizBsdBinding
import com.example.quiz_app_mvvm.repositories.QuizRepo
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class JoinQuizBSDFragment : BottomSheetDialogFragment(), QuizRepo.UploadedCallBack {

    private lateinit var _binding: JoinQuizBsdBinding
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
        _binding.cancelButton.setOnClickListener {
            this.dismiss()
        }

        _binding.joinQuizButton.setOnClickListener {
            val uniqueQuizID = _binding.enterUniqueQuizId.editText?.text.toString().trim()

            if (uniqueQuizID.isNotEmpty()) {
                _binding.enterUniqueQuizId.error = null
                // this.dismiss()
                // start progress here
                DialogsUtil.showLoadingDialog(requireActivity())
                val quizRepo = QuizRepo(this)
                // Check first that if this quiz exists or not
                quizRepo.quizListCollection
                    .document(uniqueQuizID)
                    .get()
                    .addOnCompleteListener {

                        if (it.result?.exists()!!) {
                            Log.d("TAG10", "onViewCreated: chl gya ji2")
                            quizRepo.joinQuiz(uniqueQuizID)
//                            navController.navigate(R.id.action_addFragment_to_listSecFragment)
                        } else {
                            DialogsUtil.dismissDialog()
                            this.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Invalid quiz id or this quiz doesn't exist",
                                Toast.LENGTH_SHORT
                            ).show()
                            Snackbar.make(
                                _binding.root,
                                "Invalid quiz id or this quiz doesn't exist",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                // end progress in callback
            } else {
                _binding.enterUniqueQuizId.error = "Required"
            }
        }
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {
        DialogsUtil.dismissDialog()
        this.dismiss()
        if (isAdded) {
            Snackbar.make(_binding.root, "Join Successfully", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(_binding.root, "Something went wrong", Snackbar.LENGTH_LONG)
                .show()
        }
    }

    override fun isDeleted(isDeleted: Boolean) {}
}
package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.databinding.FragmentAddBinding
import com.example.quiz_app_mvvm.databinding.JoinQuizDilaogBinding
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class AddFragment : BottomSheetDialogFragment(), QuizDao.UploadedCallBack {

    private lateinit var navController: NavController
    private lateinit var fragmentAddBinding: FragmentAddBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentAddBinding = FragmentAddBinding.inflate(inflater, container, false)
        return fragmentAddBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.list_fragment_host)

        fragmentAddBinding.createQuizSelectBtn.setOnClickListener {
            navController.navigate(R.id.action_addFragment_to_createQuizFragment)
        }

        fragmentAddBinding.joinQuizSelectBtn.setOnClickListener {
            val joinQuizDialogBinding = JoinQuizDilaogBinding.inflate(LayoutInflater.from(requireContext()))
            val joinQuizDialog = AlertDialog.Builder(requireContext(), R.style.DialogStyle)
                    .setView(joinQuizDialogBinding.root)
                    .setCancelable(false)
                    .create()
            joinQuizDialog.show()

            joinQuizDialogBinding.cancelJoinBtn.setOnClickListener {
                joinQuizDialog.dismiss()
            }

            joinQuizDialogBinding.joinQuizButton.setOnClickListener {
                val uniqueQuizID = joinQuizDialogBinding.enterUniqueQuizId.editText?.text.toString().trim()

                if (uniqueQuizID.isNotEmpty()) {
                    joinQuizDialogBinding.enterUniqueQuizId.error = null
                    joinQuizDialog.dismiss()

                    // start progress here
                    DialogsUtil.showLoadingDialog(requireContext())
                    val quizDao = QuizDao(this)
                    // Check first that if this quiz exists or not
                    quizDao.quizListCollection
                            .document(uniqueQuizID)
                            .get()
                            .addOnCompleteListener {

                                if (it.result?.exists()!!) {
                                    Log.d("TAG10", "onViewCreated: chl gya ji2")
                                    quizDao.joinQuiz(uniqueQuizID)
                                    navController.navigate(R.id.action_addFragment_to_listSecFragment)
                                } else {
                                    DialogsUtil.dismissDialog()
                                    this.dismiss()
                                    Toast.makeText(requireContext(), "Invalid quiz id or this quiz doesn't exist", Toast.LENGTH_SHORT).show()
                                    Snackbar.make(fragmentAddBinding.root, "Invalid quiz id or this quiz doesn't exist", Snackbar.LENGTH_LONG).show()
                                }
                            }
                    // end progress in callback
                } else {
                    joinQuizDialogBinding.enterUniqueQuizId.error = "Required"
                }
            }
        }
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {
        DialogsUtil.dismissDialog()
        if (isAdded) {
            Snackbar.make(fragmentAddBinding.root, "Join Successfully", Snackbar.LENGTH_LONG).show()
        } else {
            Snackbar.make(fragmentAddBinding.root, "Something went wrong", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun isDeleted(isDeleted: Boolean) {
//        TODO("Not yet implemented")
    }
}
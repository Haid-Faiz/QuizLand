package com.example.quiz_app_mvvm.utilities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.androchef.happytimer.countdowntimer.NormalCountDownView
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.*
import com.example.quiz_app_mvvm.model.MyResult

class DialogsUtil {

    companion object {

        private lateinit var loadingDialog: AlertDialog

        fun showShareIDDialog(context: Context, docID: String, activity: FragmentActivity?) {

            val shareCustomDialogBinding = ShareCustomDialogBinding.inflate(LayoutInflater.from(context))

            val alertDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setView(shareCustomDialogBinding.root)
                    .setCancelable(true)
                    .create()
            alertDialog.show()

            shareCustomDialogBinding.copyIdButton.setOnClickListener {

                val clipboardManager: ClipboardManager = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData: ClipData = ClipData.newPlainText("UniqueQuizID", docID)
                clipboardManager.setPrimaryClip(clipData)
                alertDialog.dismiss()
                Toast.makeText(context, "ID copied", Toast.LENGTH_SHORT).show()
            }

            shareCustomDialogBinding.shareIdButton.setOnClickListener {
                alertDialog.dismiss()
                val shareIntent = Intent(Intent.ACTION_SEND).setType("text/plain")
                shareIntent.putExtra(Intent.EXTRA_TEXT, docID)
                val chooser: Intent = Intent.createChooser(shareIntent, "Share unique ID with...")
                context.startActivity(chooser)
            }
        }

        fun showDeleteDialog(context: Context, function: () -> Unit) {
            val deleteDialogBinding = DeleteDialogBinding.inflate(LayoutInflater.from(context))
            val deleteDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setView(deleteDialogBinding.root)
                    .setCancelable(false)
                    .create()
            deleteDialog.show()

            deleteDialogBinding.dontDeleteBtn.setOnClickListener {
                deleteDialog.dismiss()
            }

            deleteDialogBinding.deleteYesBtn.setOnClickListener {
                function()
                deleteDialog.dismiss()
            }

        }

        fun showExitQuizDialog(context: Context, navController: NavController, normalCountDownView: NormalCountDownView) {

            val exitQuizDialogBinding = ExitQuizDialogBinding.inflate(LayoutInflater.from(context))
            val alertDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setView(exitQuizDialogBinding.root)
                    .setCancelable(false)
                    .create()
            alertDialog.show()
            exitQuizDialogBinding.exitYesBtn.setOnClickListener {
                normalCountDownView.stopTimer()
                navController.navigateUp()
                alertDialog.dismiss()
            }
            exitQuizDialogBinding.continueBtn.setOnClickListener { alertDialog.dismiss() }

        }

        fun showLoadingDialog(context: Context) {
            val loadingDialogBinding = LoadingDialogBinding.inflate(LayoutInflater.from(context))
            loadingDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setCancelable(false)
                    .setView(loadingDialogBinding.root)
                    .create()
            // start progress here
            loadingDialog.show()
        }

        fun dismissDialog(){
            loadingDialog.dismiss()
        }

        fun showMyResultDetail(context: Context, myResult: MyResult) {
            val myResultDetailsBinding = MyResultDetailsBinding.inflate(LayoutInflater.from(context))
            // setting result data through Data Binding
            myResultDetailsBinding.myResult = myResult
            val myResultDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setCancelable(true)
                    .setView(myResultDetailsBinding.root)
                    .create()
            myResultDialog.show()

            myResultDetailsBinding.detailResultOkBtn.setOnClickListener {
                myResultDialog.dismiss()
            }
        }

        fun showParticipantDetailResult(context: Context, result: MyResult) {
            val participantResultDetailsBinding = ParticipantResultDetailsBinding.inflate(LayoutInflater.from(context))
            // setting result data through data binding
            participantResultDetailsBinding.myResult = result
            val participantResultDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setCancelable(true)
                    .setView(participantResultDetailsBinding.root)
                    .create()
            participantResultDialog.show()

            participantResultDetailsBinding.participantDetailResultOkBtn.setOnClickListener {
                participantResultDialog.dismiss()
            }
        }

        fun showTimeUpDialog(context: Context, function: () -> Unit) {
            val timeUpDialogBinding = TimeUpDialogBinding.inflate(LayoutInflater.from(context))
            val timesUpDialog = AlertDialog.Builder(context, R.style.DialogStyle)
                    .setView(timeUpDialogBinding.root)
                    .setCancelable(false)
                    .create()
            timesUpDialog.show()

            timeUpDialogBinding.timeUpGotoResultBtn.setOnClickListener {
                function.invoke()
                timesUpDialog.dismiss()
            }
        }
    }
}
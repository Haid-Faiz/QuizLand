package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.adapter.QuizListAdapter
import com.example.quiz_app_mvvm.adapter.QuizListAdapter.OnQuizListItemClicked
import com.example.quiz_app_mvvm.broadcast.QuizBroadcastReceiver
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.databinding.FragmentJoinedQuizzesBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.example.quiz_app_mvvm.views.fragments.ListFragment.Companion.helloCheck
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.snackbar.Snackbar

class JoinedQuizzesFragment : Fragment(), OnQuizListItemClicked, QuizDao.UploadedCallBack {

    private lateinit var quizListViewModel: QuizListViewModel
    private var quizListAdapter: QuizListAdapter? = null

    //    private lateinit var quizListModels: ArrayList<QuizModel>
    private lateinit var fragmentJoinedQuizzesBinding: FragmentJoinedQuizzesBinding
    private lateinit var fadeInAnim: Animation
    private lateinit var fadeOutAnim: Animation
    private lateinit var arr: ObservableSnapshotArray<QuizModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentJoinedQuizzesBinding = FragmentJoinedQuizzesBinding.inflate(inflater, container, false)
        return fragmentJoinedQuizzesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        fragmentJoinedQuizzesBinding.joinedPageRecyclerview.layoutManager = LinearLayoutManager(context)
        fragmentJoinedQuizzesBinding.joinedPageRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()
        quizListViewModel.getParticipatedQuizOptions().observe(viewLifecycleOwner, {

            arr = it.snapshots
            quizListAdapter = QuizListAdapter(it, this) { itemCount: Int ->
                onListItemChanged(itemCount)
            }
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.adapter = quizListAdapter
            quizListAdapter?.startListening()
            // Fade out the progress bar & show recyclerview
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.startAnimation(fadeInAnim)
            fragmentJoinedQuizzesBinding.joinedPageProgressBar.startAnimation(fadeOutAnim)
        })
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            fragmentJoinedQuizzesBinding.joinedQuizListEmptyBottle.visibility = View.VISIBLE
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.visibility = View.INVISIBLE
        } else {
            fragmentJoinedQuizzesBinding.joinedQuizListEmptyBottle.visibility = View.INVISIBLE
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.visibility = View.VISIBLE
        }
    }

    override fun onQuizItemClicked(position: Int) {
        quizListViewModel.setQuizData(arr[position])
        helloCheck(position)
    }

    override fun onUnEnrolClicked(adapterPosition: Int) {
        QuizDao().unEnrolQuiz(arr[adapterPosition].quiz_id)
        // now it will give callback through isDeleted overridden method that weather it has been deleted or not
    }

    override fun isDeleted(isDeleted: Boolean) {
        Log.d("check", "isDeleted: chl gya")  // ye method nhi chlta
        if (isDeleted) {
            Snackbar.make(fragmentJoinedQuizzesBinding.root, "Successfully UnEnrolled", Snackbar.LENGTH_LONG).show()
        } else
            Snackbar.make(fragmentJoinedQuizzesBinding.root, "Something went wrong", Snackbar.LENGTH_LONG).show()
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {
        // this method will triggered when user will join the quiz
        if (isAdded) {
            Snackbar.make(fragmentJoinedQuizzesBinding.root, "Successfully Joined the quiz", Snackbar.LENGTH_LONG).show()
        } else
            Snackbar.make(fragmentJoinedQuizzesBinding.root, "Something went wrong", Snackbar.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        quizListAdapter?.let {
            it.stopListening()
        }
    }

//    override fun onConnectionReceive(isConnected: Boolean) {
//        DialogsUtil.showConnectionErrorDialog(requireContext(), isConnected)
//    }
}
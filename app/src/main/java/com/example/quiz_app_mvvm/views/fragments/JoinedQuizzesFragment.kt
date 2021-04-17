package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.adapter.QuizListAdapter
import com.example.quiz_app_mvvm.adapter.QuizListAdapter.OnQuizListItemClicked
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.databinding.FragmentJoinedQuizzesBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.example.quiz_app_mvvm.views.fragments.ListFragment.Companion.helloCheck
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.snackbar.Snackbar

class JoinedQuizzesFragment : Fragment(), OnQuizListItemClicked, QuizDao.UploadedCallBack {

    private lateinit var quizListViewModel: QuizListViewModel
    private var quizListAdapter: QuizListAdapter? = null
    private lateinit var quizDao: QuizDao
    private lateinit var fragmentJoinedQuizzesBinding: FragmentJoinedQuizzesBinding
    private lateinit var arr: ObservableSnapshotArray<QuizModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentJoinedQuizzesBinding = FragmentJoinedQuizzesBinding.inflate(inflater, container, false)
        return fragmentJoinedQuizzesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizDao = QuizDao(this)
        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        fragmentJoinedQuizzesBinding.joinedPageRecyclerview.layoutManager = LinearLayoutManager(context)
        fragmentJoinedQuizzesBinding.joinedPageRecyclerview.setHasFixedSize(true)

    }


    override fun onStart() {
        super.onStart()
        Log.d("TAG1", "onStart: called")
        quizListViewModel.getParticipatedQuizOptions()
        quizListViewModel.participatedQuizOptions.observe(viewLifecycleOwner, {

            fragmentJoinedQuizzesBinding.joinedPageProgressBar.isVisible = true
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.isVisible = false

            arr = it.snapshots
            quizListAdapter = QuizListAdapter(it, this)
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.adapter = quizListAdapter
            quizListAdapter?.startListening()
            // Fade out the progress bar & show recyclerview
        })
    }

    override fun onListItemChanged(itemCount: Int) {
        fragmentJoinedQuizzesBinding.joinedPageProgressBar.isVisible = false
        fragmentJoinedQuizzesBinding.joinedPageRecyclerview.isVisible = true

        if (itemCount == 0) {
            fragmentJoinedQuizzesBinding.joinedQuizListEmptyBottle.isVisible = true
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.isVisible = false
        } else {
            fragmentJoinedQuizzesBinding.joinedQuizListEmptyBottle.isVisible = false
            fragmentJoinedQuizzesBinding.joinedPageRecyclerview.isVisible = true
        }
    }


    override fun onQuizItemClicked(position: Int) {
        quizListViewModel.setQuizData(arr[position])
        helloCheck(position)
    }

    override fun onUnEnrolClicked(adapterPosition: Int) {
        quizDao.unEnrolQuiz(arr[adapterPosition].quiz_id)
        // now it will give callback through isDeleted overridden
        // method that weather it has been deleted or not
    }

    override fun isDeleted(isDeleted: Boolean) {
        if (isDeleted) {
            Snackbar.make(fragmentJoinedQuizzesBinding.root, "Successfully UnEnrolled", Snackbar.LENGTH_SHORT).show()
        } else
            Snackbar.make(fragmentJoinedQuizzesBinding.root, "Something went wrong", Snackbar.LENGTH_SHORT).show()
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
        Log.d("TAG", "onStop: called")

//        quizListViewModel.participatedQuizOptions?.removeObservers(this)
    }
}
package com.example.quiz_app_mvvm.ui.quiz.join

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.quiz_app_mvvm.ui.quiz.QuizListAdapter
import com.example.quiz_app_mvvm.ui.quiz.QuizListAdapter.OnQuizListItemClicked
import com.example.quiz_app_mvvm.repositories.QuizRepo
import com.example.quiz_app_mvvm.databinding.FragmentJoinedQuizzesBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.ClickListeners
import com.example.quiz_app_mvvm.ui.quiz.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.snackbar.Snackbar

class JoinedQuizzesFragment : Fragment(), OnQuizListItemClicked, QuizRepo.UploadedCallBack {

    private lateinit var quizListViewModel: QuizListViewModel
    private var quizListAdapter: QuizListAdapter? = null
    private lateinit var quizRepo: QuizRepo
    private lateinit var _binding: FragmentJoinedQuizzesBinding
    private lateinit var arr: ObservableSnapshotArray<QuizModel>
    private lateinit var a: ClickListeners

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentJoinedQuizzesBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quizRepo = QuizRepo(this)
        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        _binding.joinedPageRecyclerview.setHasFixedSize(true)
    }


    override fun onStart() {
        super.onStart()
        quizListViewModel.getParticipatedQuizOptions()
        quizListViewModel.participatedQuizOptions.observe(viewLifecycleOwner, {

            _binding.joinedPageProgressBar.isVisible = true
            _binding.joinedPageRecyclerview.isVisible = false

            arr = it.snapshots
            quizListAdapter = QuizListAdapter(it, this)
            _binding.joinedPageRecyclerview.adapter = quizListAdapter
            quizListAdapter?.startListening()
            // Fade out the progress bar & show recyclerview
        })
    }

    override fun onListItemChanged(itemCount: Int) {
        _binding.joinedPageProgressBar.isVisible = false
        _binding.joinedPageRecyclerview.isVisible = true

        if (itemCount == 0) {
            _binding.joinedQuizListEmptyBottle.isVisible = true
            _binding.joinedPageRecyclerview.isVisible = false
        } else {
            _binding.joinedQuizListEmptyBottle.isVisible = false
            _binding.joinedPageRecyclerview.isVisible = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        a = context as ClickListeners
    }

    override fun onQuizItemClicked(position: Int) {
        quizListViewModel.setQuizData(arr[position])
        a.oViewQuizClicked(position)
    }

    override fun onUnEnrolClicked(adapterPosition: Int) {
        quizRepo.unEnrolQuiz(arr[adapterPosition].quiz_id)
        // now it will give callback through isDeleted overridden
        // method that weather it has been deleted or not
    }

    override fun isDeleted(isDeleted: Boolean) {
        if (isDeleted) {
            Snackbar.make(
                _binding.root,
                "Successfully UnEnrolled",
                Snackbar.LENGTH_SHORT
            ).show()
        } else
            Snackbar.make(
                _binding.root,
                "Something went wrong",
                Snackbar.LENGTH_SHORT
            ).show()
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {
        // this method will triggered when user will join the quiz
        if (isAdded) {
            Snackbar.make(
                _binding.root,
                "Successfully Joined the quiz",
                Snackbar.LENGTH_LONG
            ).show()
        } else
            Snackbar.make(
                _binding.root,
                "Something went wrong",
                Snackbar.LENGTH_LONG
            ).show()
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
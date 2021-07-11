package com.example.quiz_app_mvvm.ui.quiz.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.repositories.QuizRepo
import com.example.quiz_app_mvvm.databinding.FragmentCreatedQuizesBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.ui.quiz.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.snackbar.Snackbar


class CreatedQuizesFragment : Fragment(), CreatedQuizzesAdapter.OnCreatedQuizItemClicked, QuizRepo.UploadedCallBack {

    //    private val quizListViewModel: QuizListViewModel by activityViewModels()
//    private val quizListViewModel: QuizListViewModel by viewModels()
    private lateinit var quizListViewModel: QuizListViewModel
    private lateinit var createdListAdapter: CreatedQuizzesAdapter
    private lateinit var binding: FragmentCreatedQuizesBinding
    private lateinit var navController: NavController
    private lateinit var arr: ObservableSnapshotArray<QuizModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentCreatedQuizesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.createdQuizBackBtn.setOnClickListener {
            navController.popBackStack()
        }

        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
//        fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
//        fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        binding.createdQuizRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.createdQuizRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        quizListViewModel.getMyCreatedQuizzes()
        quizListViewModel.createdQuizOptions.observe(viewLifecycleOwner, {
            binding.createdProgressBar.isVisible = true
            binding.createdQuizRecyclerview.isVisible = false

            arr = it.snapshots
            createdListAdapter = CreatedQuizzesAdapter(it, this)
            createdListAdapter.startListening()
            binding.createdQuizRecyclerview.adapter = createdListAdapter
        })
    }

    override fun onListItemChanged(itemCount: Int) {

        binding.createdProgressBar.isVisible = false
        binding.createdQuizRecyclerview.isVisible = true

        if (itemCount == 0) {
            binding.emptyListView.isVisible = true
            binding.createdQuizRecyclerview.isVisible = false
        } else {
            binding.emptyListView.isVisible = false
            binding.createdQuizRecyclerview.isVisible = true
        }
    }

    override fun onStop() {
        super.onStop()
        createdListAdapter.stopListening()
    }

    override fun onGetResultClicked(position: Int) {
        // passing quizModel data through ViewModel to AdminResultFragment
        quizListViewModel.setQuizData(arr[position])
        navController.navigate(R.id.action_createdQuizesFragment_to_adminResultFragment)
    }

    override fun onDeleteClicked(position: Int) {
        // **Note** -> when we only have single functional parameter then we move that single lambda argument out of parenthesis
        DialogsUtil.showDeleteDialog(requireContext()) {
            // delete the quiz from MyCreated collection and from QuizList collection
            val quizDao = QuizRepo()
            quizDao.deleteQuiz(arr[position].quiz_id)
            // now it will give callback through isDeleted overridden method that weather it has been deleted or not
        }
    }

    override fun onShareQuizClicked(position: Int) {
        DialogsUtil.showShareIDDialog(requireContext(), arr[position].quiz_id, requireActivity())
    }

    override fun isDeleted(isDeleted: Boolean) {
        if (isDeleted)
            Snackbar.make(binding.root, "Successfully Deleted", Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(binding.root, "Oops.. Something went wrong", Snackbar.LENGTH_LONG).show()
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {

    }

}
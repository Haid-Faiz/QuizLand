package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.adapter.CreatedQuizzesAdapter
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.databinding.FragmentCreatedQuizesBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray
import com.google.android.material.snackbar.Snackbar


class CreatedQuizesFragment : Fragment(), CreatedQuizzesAdapter.OnCreatedQuizItemClicked, QuizDao.UploadedCallBack {

    //    private val quizListViewModel: QuizListViewModel by activityViewModels()
//    private val quizListViewModel: QuizListViewModel by viewModels()
    private lateinit var quizListViewModel: QuizListViewModel
    private lateinit var createdListAdapter: CreatedQuizzesAdapter

    //    private lateinit var quizListModels: ArrayList<QuizModel>
    private lateinit var fadeInAnim: Animation
    private lateinit var fadeOutAnim: Animation
    private lateinit var fragmentCreatedQuizesBinding: FragmentCreatedQuizesBinding
    private lateinit var navController: NavController
    private lateinit var arr: ObservableSnapshotArray<QuizModel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentCreatedQuizesBinding = FragmentCreatedQuizesBinding.inflate(inflater, container, false)
        return fragmentCreatedQuizesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        fragmentCreatedQuizesBinding.createdQuizBackBtn.setOnClickListener {
            navController.popBackStack()
        }

        quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)

        fragmentCreatedQuizesBinding.createdQuizRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        fragmentCreatedQuizesBinding.createdQuizRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        quizListViewModel.getMyCreatedQuizzes().observe(viewLifecycleOwner, {

            arr = it.snapshots
            createdListAdapter = CreatedQuizzesAdapter(it, this) { itemCount: Int ->
                onListItemChanged(itemCount)
            }
            createdListAdapter.startListening()

            fragmentCreatedQuizesBinding.createdQuizRecyclerview.adapter = createdListAdapter

            fragmentCreatedQuizesBinding.createdQuizRecyclerview.visibility = View.VISIBLE
            fragmentCreatedQuizesBinding.createdProgressBar.visibility = View.INVISIBLE
        })
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            fragmentCreatedQuizesBinding.emptyListView.visibility = View.VISIBLE
            fragmentCreatedQuizesBinding.createdQuizRecyclerview.visibility = View.INVISIBLE
        } else {
            fragmentCreatedQuizesBinding.emptyListView.visibility = View.INVISIBLE
            fragmentCreatedQuizesBinding.createdQuizRecyclerview.visibility = View.VISIBLE
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
            val quizDao = QuizDao()
            quizDao.deleteQuiz(arr[position].quiz_id)
            // now it will give callback through isDeleted overridden method that weather it has been deleted or not
        }
    }

    override fun onShareQuizClicked(position: Int) {
        DialogsUtil.showShareIDDialog(requireContext(), arr[position].quiz_id, requireActivity())
    }

    override fun isDeleted(isDeleted: Boolean) {
        if (isDeleted)
            Snackbar.make(fragmentCreatedQuizesBinding.root, "Successfully Deleted", Snackbar.LENGTH_LONG).show()
        else
            Snackbar.make(fragmentCreatedQuizesBinding.root, "Oops.. Something went wrong", Snackbar.LENGTH_LONG).show()
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {
        // TODO("Not yet implemented")
    }

}
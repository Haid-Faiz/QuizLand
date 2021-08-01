package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.databinding.FragmentMyResultsBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyResultsFragment : Fragment(), MyResultsAdapter.OnMyResultClicked {

    private lateinit var _binding: FragmentMyResultsBinding
    private val quizViewModel: QuizViewModel by viewModels()
    private lateinit var myResultsAdapter: MyResultsAdapter
    private lateinit var observableSnapshotArray: ObservableSnapshotArray<MyResult>
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyResultsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        _binding.myResultsBackBtn.setOnClickListener { navController.popBackStack() }
        _binding.myResultRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        _binding.myResultRecyclerview.setHasFixedSize(true)
        _binding.retryButton.setOnClickListener { quizViewModel.getMyResults() }
    }

    override fun onStart() {
        super.onStart()
        quizViewModel.getMyResults()
        quizViewModel.resultList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showSnackBar(message = it.message!!)
                    _binding.statusBox.isVisible = true
                    _binding.myResultsProgressBar.isVisible = false
                    _binding.myResultRecyclerview.isVisible = false
                }
                is Resource.Loading -> {
                    _binding.myResultsProgressBar.isVisible = true
                    _binding.myResultRecyclerview.isVisible = false
                    _binding.statusBox.isVisible = false
                }
                is Resource.Success -> {
                    _binding.myResultsProgressBar.isVisible = false
                    _binding.myResultRecyclerview.isVisible = true
                    _binding.statusBox.isVisible = false
                    val options = FirestoreRecyclerOptions.Builder<MyResult>()
                        .setQuery(it.data?.query!!, MyResult::class.java)
                        .build()

                    observableSnapshotArray = options.snapshots
                    myResultsAdapter = MyResultsAdapter(options, this) { itemCount ->
                        onListItemChanged(itemCount)
                    }
                    myResultsAdapter.startListening()
                    _binding.myResultRecyclerview.adapter = myResultsAdapter
                    _binding.myResultsProgressBar.isVisible = false
                }
            }
        }
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            _binding.emptyResultListView.isVisible = true
            _binding.myResultRecyclerview.isVisible = false
        } else {
            _binding.emptyResultListView.isVisible = false
        }
    }

    override fun onStop() {
        super.onStop()
        myResultsAdapter.stopListening()
    }

    override fun onResultDetailClicked(position: Int) {
        DialogsUtil.showMyResultDetail(requireContext(), observableSnapshotArray[position])
    }
}
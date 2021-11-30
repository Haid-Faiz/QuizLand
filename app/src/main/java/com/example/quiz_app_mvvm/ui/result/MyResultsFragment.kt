package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.databinding.FragmentMyResultsBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyResultsFragment : Fragment(), MyResultsAdapter.OnMyResultClicked {

    private var _binding: FragmentMyResultsBinding? = null
    private val binding get() = _binding!!
    private val quizViewModel: QuizViewModel by viewModels()
    private var myResultsAdapter: MyResultsAdapter? = null
    private lateinit var observableSnapshotArray: ObservableSnapshotArray<MyResult>
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.myResultsBackBtn.setOnClickListener { navController.popBackStack() }
        binding.myResultRecyclerview.setHasFixedSize(true)
        binding.retryButton.setOnClickListener { quizViewModel.getMyResults() }
    }

    override fun onStart() {
        super.onStart()
        quizViewModel.getMyResults()
        quizViewModel.resultList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> {
                    showSnackBar(message = it.message!!)
                    binding.statusBox.isVisible = true
                    binding.myResultsProgressBar.isVisible = false
                    binding.myResultRecyclerview.isVisible = false
                    binding.emptyResultListView.isVisible = false
                }
                is Resource.Loading -> {
                    binding.myResultsProgressBar.isVisible = true
                    binding.myResultRecyclerview.isVisible = false
                    binding.statusBox.isVisible = false
                    binding.emptyResultListView.isVisible = false
                }
                is Resource.Success -> {
                    binding.myResultsProgressBar.isVisible = false
                    binding.statusBox.isVisible = false

                    if (it.data!!.isEmpty) {
                        binding.emptyResultListView.isVisible = true
                        binding.myResultRecyclerview.isVisible = false
                    } else {
                        binding.myResultRecyclerview.isVisible = true
                        binding.emptyResultListView.isVisible = false

                        val options = FirestoreRecyclerOptions.Builder<MyResult>()
                            .setQuery(it.data.query, MyResult::class.java)
                            .build()

                        observableSnapshotArray = options.snapshots
                        myResultsAdapter = MyResultsAdapter(options, this)
                        myResultsAdapter?.startListening()
                        binding.myResultRecyclerview.adapter = myResultsAdapter
                        binding.myResultsProgressBar.isVisible = false
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        myResultsAdapter?.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResultDetailClicked(position: Int) {
        DialogsUtil.showMyResultDetail(requireContext(), observableSnapshotArray[position])
    }
}

package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.databinding.FragmentMyResultsBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray

class MyResultsFragment : Fragment(), MyResultsAdapter.OnMyResultClicked {

    private lateinit var _binding: FragmentMyResultsBinding
//    private val viewModel: QuizListViewModel by viewModels()
    private lateinit var myResultsAdapter: MyResultsAdapter
    private lateinit var observableSnapshotArray: ObservableSnapshotArray<MyResult>
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyResultsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        _binding.myResultsBackBtn.setOnClickListener {
            navController.popBackStack()
        }

        _binding.myResultRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        _binding.myResultRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        val viewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        viewModel.getMyResults()
        viewModel.myResultOptions.observe(viewLifecycleOwner, {

            observableSnapshotArray = it.snapshots
            // invisible progress bar here
            myResultsAdapter = MyResultsAdapter(it, this) { itemCount: Int ->
                onListItemChanged(itemCount)
            }
            myResultsAdapter.startListening()
            _binding.myResultRecyclerview.adapter = myResultsAdapter
            _binding.myResultRecyclerview.visibility= View.VISIBLE
            _binding.myResultsProgressBar.visibility = View.INVISIBLE
        })
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            _binding.emptyResultListView.visibility = View.VISIBLE
            _binding.myResultRecyclerview.visibility = View.INVISIBLE
        } else {
            _binding.emptyResultListView.visibility = View.INVISIBLE
            _binding.myResultRecyclerview.visibility = View.VISIBLE
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
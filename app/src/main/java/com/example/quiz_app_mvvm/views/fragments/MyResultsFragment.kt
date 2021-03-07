package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.adapter.MyResultsAdapter
import com.example.quiz_app_mvvm.databinding.FragmentMyResultsBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.firebase.ui.firestore.ObservableSnapshotArray

class MyResultsFragment : Fragment(), MyResultsAdapter.OnMyResultClicked {

    private lateinit var fragmentMyResultsBinding: FragmentMyResultsBinding
//    private val viewModel: QuizListViewModel by viewModels()
    private lateinit var myResultsAdapter: MyResultsAdapter
    private lateinit var observableSnapshotArray: ObservableSnapshotArray<MyResult>
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentMyResultsBinding = FragmentMyResultsBinding.inflate(inflater, container, false)
        return fragmentMyResultsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        fragmentMyResultsBinding.myResultsBackBtn.setOnClickListener {
            navController.popBackStack()
        }

        fragmentMyResultsBinding.myResultRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        fragmentMyResultsBinding.myResultRecyclerview.setHasFixedSize(true)
    }

    override fun onStart() {
        super.onStart()

        val viewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        viewModel.getMyResults().observe(viewLifecycleOwner, {


            observableSnapshotArray = it.snapshots
            // invisible progress bar here
            myResultsAdapter = MyResultsAdapter(it, this) { itemCount: Int ->
                onListItemChanged(itemCount)
            }
            myResultsAdapter.startListening()
            fragmentMyResultsBinding.myResultRecyclerview.adapter = myResultsAdapter
            fragmentMyResultsBinding.myResultRecyclerview.visibility= View.VISIBLE
            fragmentMyResultsBinding.myResultsProgressBar.visibility = View.INVISIBLE
        })
    }

    private fun onListItemChanged(itemCount: Int) {
        if (itemCount == 0) {
            fragmentMyResultsBinding.emptyResultListView.visibility = View.VISIBLE
            fragmentMyResultsBinding.myResultRecyclerview.visibility = View.INVISIBLE
        } else {
            fragmentMyResultsBinding.emptyResultListView.visibility = View.INVISIBLE
            fragmentMyResultsBinding.myResultRecyclerview.visibility = View.VISIBLE
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
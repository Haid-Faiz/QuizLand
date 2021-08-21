package com.example.quiz_app_mvvm.ui.quiz.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentCreatedQuizesBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.ObservableSnapshotArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatedQuizesFragment : Fragment(), CreatedQuizzesAdapter.OnCreatedQuizItemClicked {

    private val quizViewModel: QuizViewModel by activityViewModels()
    private lateinit var createdListAdapter: CreatedQuizzesAdapter
    private var _binding: FragmentCreatedQuizesBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var arr: ObservableSnapshotArray<QuizModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatedQuizesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        binding.createdQuizBackBtn.setOnClickListener { navController.popBackStack() }
//        fadeInAnim = AnimationUtils.loadAnimation(context, R.anim.fade_in)
//        fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        binding.createdQuizRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.createdQuizRecyclerview.setHasFixedSize(true)
        binding.retryButton.setOnClickListener {
            quizViewModel.getMyCreatedQuizzes()
        }
    }

    override fun onStart() {
        super.onStart()
        quizViewModel.getMyCreatedQuizzes()
        quizViewModel.quizList.observe(viewLifecycleOwner) {

            when (it) {
                is Resource.Error -> {
                    showSnackBar(message = it.message!!)
                    binding.statusBox.isVisible = true
                    binding.emptyListView.isVisible = false
                    binding.createdProgressBar.isVisible = false
                    binding.createdQuizRecyclerview.isVisible = false
                }
                is Resource.Loading -> {
                    binding.createdProgressBar.isVisible = true
                    binding.createdQuizRecyclerview.isVisible = false
                    binding.statusBox.isVisible = false
                    binding.emptyListView.isVisible = false
                }
                is Resource.Success -> {
                    binding.statusBox.isVisible = false
                    binding.createdProgressBar.isVisible = false
                    if (it.data!!.isEmpty) {
                        binding.createdQuizRecyclerview.isVisible = false
                        binding.emptyListView.isVisible = true
                    } else {
                        binding.createdQuizRecyclerview.isVisible = true
                        binding.emptyListView.isVisible = false
                        val options = FirestoreRecyclerOptions.Builder<QuizModel>()
                            .setQuery(it.data.query, QuizModel::class.java)
                            .build()

                        arr = options.snapshots
                        createdListAdapter = CreatedQuizzesAdapter(options, this)
                        createdListAdapter.startListening()
                        binding.createdQuizRecyclerview.adapter = createdListAdapter
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        createdListAdapter.stopListening()
    }

    override fun onGetResultClicked(position: Int) {
        // passing quizModel data through ViewModel to AdminResultFragment
        quizViewModel.setQuizData(arr[position])
        navController.navigate(R.id.action_createdQuizesFragment_to_adminResultFragment)
    }

    override fun onDeleteClicked(position: Int) {
        DialogsUtil.showDeleteDialog(requireContext()) {
            // delete the quiz from MyCreated collection and from QuizList collection
            lifecycleScope.launchWhenCreated {
                quizViewModel.deleteCreatedQuiz(arr[position].quiz_id).let {
                    when (it) {
                        is Resource.Error -> showSnackBar(message = "Something went wrong")
                        is Resource.Loading -> TODO()
                        is Resource.Success -> showSnackBar(message = "Successfully Deleted")
                    }
                }
            }
        }
    }

    override fun onShareQuizClicked(position: Int) {
        DialogsUtil.showShareIDDialog(requireContext(), arr[position].quiz_id, requireActivity())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

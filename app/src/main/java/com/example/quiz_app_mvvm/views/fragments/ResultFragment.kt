package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentResultBinding
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel

class ResultFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var fragmentResultBinding: FragmentResultBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentResultBinding = FragmentResultBinding.inflate(layoutInflater, container, false)
        return fragmentResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        fragmentResultBinding.resultHomeBtn.setOnClickListener {
            navController.navigate(R.id.action_resultFragment_to_listFragment)
        }

//        val args by navArgs<ResultFragmentArgs>()
//        quizId = args.quizId
//        quizId = ResultFragmentArgs.fromBundle(arguments!!).quizId

        val viewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        // get results
        val result = viewModel.getResult()
        loadAnimation(result.scoredPercent)
        fragmentResultBinding.resultCorrectText.text = result.correctAns.toString()
        fragmentResultBinding.resultWrongText.text = result.wrongAns.toString()
        fragmentResultBinding.resultMissedText.text = result.missedAns.toString()
        fragmentResultBinding.resultPercent.text = "${result.scoredPercent.toString()} %" //percent.toString() + "%"
        fragmentResultBinding.resultProgress.progress = result.scoredPercent.toInt()
    }

    private fun loadAnimation(percent: Long) {
        if (percent >= 60) {
            fragmentResultBinding.fireworksAnimation.visibility = View.VISIBLE
            fragmentResultBinding.resultFeedback.text = "Yay! you scored ${percent}"
        }
    }
}
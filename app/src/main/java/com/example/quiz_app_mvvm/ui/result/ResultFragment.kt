package com.example.quiz_app_mvvm.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentResultBinding
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var _binding: FragmentResultBinding
    private val quizViewModel : QuizViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        _binding.resultHomeBtn.setOnClickListener {
            navController.navigate(R.id.action_resultFragment_to_listFragment)
        }

//        val args by navArgs<ResultFragmentArgs>()
//        quizId = args.quizId
//        quizId = ResultFragmentArgs.fromBundle(arguments!!).quizId

        // get results
        val result = quizViewModel.getResult()
        loadAnimation(result.scoredPercent, result.marksScored, result.totalMarks)
        _binding.resultCorrectText.text = result.correctAns.toString()
        _binding.resultWrongText.text = result.wrongAns.toString()
        _binding.resultMissedText.text = result.missedAns.toString()
        _binding.resultPercent.text = "${String.format("%.1f", result.scoredPercent)}%"              //percent.toString() + "%"
        _binding.resultProgress.progress = result.scoredPercent.toInt()
    }

    private fun loadAnimation(percent: Float, marksScored: Float, totalMarks: Float) {
        if (percent >= 60) {
            _binding.fireworksAnimation.isVisible = true
            _binding.resultFeedback.text = "Yay! you scored ${String.format("%.1f", marksScored)} out of ${String.format("%.1f", totalMarks)}"
        } else {
            _binding.resultFeedback.text = "You scored ${String.format("%.1f", marksScored)} out of ${String.format("%.1f", totalMarks)}"
            _binding.lowResultFeedback.isVisible = true
        }
    }
}
package com.example.quiz_app_mvvm.ui.quiz.join

import androidx.navigation.NavController
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.quiz_app_mvvm.databinding.FragmentDetailsBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private val quizViewModel: QuizViewModel by activityViewModels()
    private var position = 0
    private var totalQuestions = 0
    private lateinit var quizData: QuizModel
    private lateinit var quizId: String
    private lateinit var quizName: String
    private lateinit var navController: NavController
    private lateinit var _binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        quizData = quizViewModel.getQuizData()
        _binding.quiz = quizData

//        In Java --->
//        position = DetailsFragmentArgs.fromBundle(getArguments()).position
//        In Kotlin --->
//        val args by navArgs<DetailsFragmentArgs>()
        val args: DetailsFragmentArgs by navArgs()
        position = args.position
        // -----------------------
        quizId = quizData.quiz_id
        totalQuestions = quizData.questions
        quizName = quizData.name

        _binding.detailsBackBtn.setOnClickListener { navController.popBackStack() }

        _binding.detailsStartBtn.setOnClickListener {
            // checking if user has already participated or not
            if (!quizData.participated) {

                // check if it is quiz time or not
                val calendar = Calendar.getInstance()
                val currentYear: Int = calendar.get(Calendar.YEAR)
                val currentMonth: Int = calendar.get(Calendar.MONTH)
                val currentDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMin = calendar.get(Calendar.MINUTE)

                val currentMinutesOfDay = currentHour * 60 + currentMin
                val startMinutes =
                    quizData.quizStartDate!!.quizStartTimeHour * 60 + quizData.quizStartDate!!.quizStartTimeMin
                val endMinutes =
                    startMinutes + quizData.quizDurationHour * 60 + quizData.quizDurationMin
                if (
                    quizData.quizStartDate?.year == currentYear
                    && quizData.quizStartDate?.month == currentMonth
                    && quizData.quizStartDate?.date == currentDay
                    && currentMinutesOfDay in startMinutes until endMinutes
                ) {
                    val action =
                        DetailsFragmentDirections.actionDetailsFragmentToQuizFragment()
                    //   action.setPosition(position);
                    action.quizDocumentID = quizId
                    action.quizName = quizName
                    //    action.setQuizModel(quizModel);
                    action.totalQuestions = totalQuestions
                    navController.navigate(action)
                } else if (currentMinutesOfDay < startMinutes)
                    Snackbar.make(
                        _binding.root,
                        "This quiz has not been started yet !",
                        Snackbar.LENGTH_LONG
                    ).show()
                else Snackbar.make(
                    _binding.root,
                    "Oops..! This quiz has been ended",
                    Snackbar.LENGTH_LONG
                ).show()

            } else Snackbar.make(
                _binding.root,
                "You have already given this quiz",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

//    private fun canJoinQuiz() {
//        val pickedCalendar = Calendar.getInstance()
//        val year: Int = quizData.quizStartDate?.year!!
//        val month: Int = quizData.quizStartDate?.month!!
//        val date: Int = quizData.quizStartDate?.date!!
//        val hourOfDay: Int = quizData.quizStartDate?.quizStartTimeHour!!
//        val minute: Int = quizData.quizStartDate?.quizStartTimeMin!!
//        pickedCalendar.set(year, month, date, hourOfDay, minute)
//        val date1 = Date(45L)
//        val date2 = Date(45L)
//    }
}
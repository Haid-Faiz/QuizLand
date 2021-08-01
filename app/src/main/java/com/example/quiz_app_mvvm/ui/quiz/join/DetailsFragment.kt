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
import com.example.quiz_app_mvvm.util.showSnackBar
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
            if (quizData.participated) showSnackBar("You have already given this quiz") else {

                val pickedCalendar = Calendar.getInstance()
                val year: Int = quizData.quizStartDate?.year!!
                val month: Int = quizData.quizStartDate?.month!!
                val date: Int = quizData.quizStartDate?.date!!
                val hourOfDay: Int = quizData.quizStartDate?.quizStartTimeHour!!
                val minute: Int = quizData.quizStartDate?.quizStartTimeMin!!
                pickedCalendar.set(year, month, date, hourOfDay, minute)
                val quizStartDate = Date(pickedCalendar.timeInMillis)
                //-----------------------------------------------------------------

                pickedCalendar.add(Calendar.HOUR_OF_DAY, quizData.quizDurationHour)
                pickedCalendar.add(Calendar.MINUTE, quizData.quizDurationMin)

                val quizEndDate = Date(pickedCalendar.timeInMillis)
                val currentDate = Date(System.currentTimeMillis())
                // quiz has not yet been started
                // quiz is over
                // running

                if (currentDate.before(quizStartDate)) showSnackBar("This quiz isn't yet started !")
                else if (currentDate.after(quizEndDate)) showSnackBar("This quiz is over now !")
                else {
                    // user can join the quiz in this time block
                    val action = DetailsFragmentDirections.actionDetailsFragmentToQuizFragment()
                    //   action.setPosition(position);
                    action.quizDocumentID = quizId
                    action.quizName = quizName
                    //    action.setQuizModel(quizModel);
                    action.totalQuestions = totalQuestions
                    navController.navigate(action)
                }
            }
        }
    }
}
package com.example.quiz_app_mvvm.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.androchef.happytimer.countdowntimer.HappyTimer
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentQuizBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.model.User
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Random
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class QuizFragment : Fragment(), View.OnClickListener {
    // UI elements
    @Inject
    lateinit var user: User
    private lateinit var navController: NavController
    private lateinit var quizId: String
    private lateinit var quizName: String
    private lateinit var quizData: QuizModel
    private var totalQuestions = 0
    private var currentQuesNum = 0
    private var correctAnswer = 0
    private var wrongAnswer = 0
    private val randomQueList: MutableList<QuestionsModel> = ArrayList()
    private var canAnswer = false
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        requireActivity().onBackPressedDispatcher.addCallback(this@QuizFragment) {
            DialogsUtil.showExitQuizDialog(
                requireContext(),
                navController,
                binding.normalCountDownView
            )
        }

        val args: QuizFragmentArgs by navArgs()
        quizId = args.quizDocumentID
        totalQuestions = args.totalQuestions
        quizName = args.quizName
        // quizId = QuizFragmentArgs.fromBundle(getArguments).quizDocumentID
        // totalQuestions = QuizFragmentArgs.fromBundle(getArguments).totalQuestions
        // quizName = QuizFragmentArgs.fromBundle(getArguments).quizName
        // -----------------------------------------
        quizData = viewModel.getQuizData()
        // -----------------------------------------
        viewModel.getQuestions(
            userId = Firebase.auth.currentUser?.uid!!,
            quizID = quizData.quiz_id
        )
        viewModel.questions.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> showSnackBar(it.message!!)
                // is Resource.Loading -> TODO()
                is Resource.Success -> {
                    // Clearing the list
                    randomQueList.clear()
                    // Getting questions
                    val questionListModel: QuestionsListModel? = it.data!!.documents[0]
                        .toObject(QuestionsListModel::class.java)
                    // Explanation ->
                    // val querySnapshot: QuerySnapshot? = it.result
                    // val a: MutableList<DocumentSnapshot>? = querySnapshot?.documents
                    // val ds: DocumentSnapshot? = a?.get(0)
                    // val questionListModel: QuestionsListModel? = ds?.toObject(QuestionsListModel::class.java)
                    val questionList: ArrayList<QuestionsModel>? = questionListModel?.questionsList
                    pickQuestion(questionList)
                    loadUI()
                }
            }
        }

        binding.apply {
            quizOptionOne.setOnClickListener(this@QuizFragment)
            quizOptionTwo.setOnClickListener(this@QuizFragment)
            quizOptionThree.setOnClickListener(this@QuizFragment)
            quizOptionFour.setOnClickListener(this@QuizFragment)
            quizNextBtn.setOnClickListener(this@QuizFragment)
            quizCloseBtn.setOnClickListener(this@QuizFragment)
        }
    }

    private fun pickQuestion(questionList: ArrayList<QuestionsModel>?) {
        for (i in questionList?.indices!!) {
            val randomNum = Random().nextInt(questionList.size) // getRandomNum(questionList.size)
            randomQueList.add(questionList[randomNum])
            questionList.removeAt(randomNum)
        }
    }

    private fun loadUI() {
        binding.quizTitle.text = quizName
        enableOptions()
        loadQuestion(1) // question no. 1
        startTimer()
    }

    private fun enableOptions() = binding.apply {
        // show all options button
        quizOptionOne.isVisible = true
        quizOptionTwo.isVisible = true
        quizOptionThree.isVisible = true
        quizOptionFour.isVisible = true
        // enable options button
        quizOptionOne.isEnabled = true
        quizOptionTwo.isEnabled = true
        quizOptionThree.isEnabled = true
        quizOptionFour.isEnabled = true
        // show next button
        quizNextBtn.isVisible = true
    }

    private fun loadQuestion(questionSerialNum: Int) = binding.apply {
        quizQuestionNum.text = "Q.No. $questionSerialNum out of ${randomQueList.size}"
        // load question text
        quizQuestion.text = randomQueList[questionSerialNum - 1].question
        // load option button
        quizOptionOne.text = randomQueList[questionSerialNum - 1].option_a
        quizOptionTwo.text = randomQueList[questionSerialNum - 1].option_b
        quizOptionThree.text = randomQueList[questionSerialNum - 1].option_c
        quizOptionFour.text = randomQueList[questionSerialNum - 1].option_d

        if (questionSerialNum == randomQueList.size) {
            quizNextBtn.text = "Submit Quiz"
        }
        // question loaded, now user can ans the question
        canAnswer = true
        currentQuesNum = questionSerialNum
    }

    private fun startTimer() {
        // setting time duration
        val endTimeInSeconds =
            (quizData.quizStartDate!!.quizStartTimeHour + quizData.quizDurationHour) * 3600 + (quizData.quizStartDate!!.quizStartTimeMin + quizData.quizDurationMin) * 60

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMin = calendar.get(Calendar.MINUTE)
        val currentSec = calendar.get(Calendar.SECOND)

        val currentTimeInSeconds = currentHour * 3600 + currentMin * 60 + currentSec
        val remainingTime = endTimeInSeconds - currentTimeInSeconds

        // Initializing Timer with seconds
        binding.normalCountDownView.initTimer(remainingTime)
        // set OnTickListener for getting updates on time.
        binding.normalCountDownView.setOnTickListener(object : HappyTimer.OnTickListener {

            override fun onTick(completedSeconds: Int, remainingSeconds: Int) {}

            override fun onTimeUp() {
                canAnswer = false
                submitResult()
                DialogsUtil.showTimeUpDialog(requireContext()) {
                    navController.navigate(R.id.action_quizFragment_to_resultFragment)
                }
            }
        })
        binding.normalCountDownView.startTimer()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.quiz_option_one -> verifyAnswer(binding.quizOptionOne)
            R.id.quiz_option_two -> verifyAnswer(binding.quizOptionTwo)
            R.id.quiz_option_three -> verifyAnswer(binding.quizOptionThree)
            R.id.quiz_option_four -> verifyAnswer(binding.quizOptionFour)
            R.id.quiz_next_btn -> {
                if (currentQuesNum == randomQueList.size) {
                    // submit results
                    binding.normalCountDownView.stopTimer() // Saved memory leak
                    submitResult()
                    navController.navigate(R.id.action_quizFragment_to_resultFragment)
                } else {
                    currentQuesNum++
                    loadQuestion(currentQuesNum)
                    resetOptions()
                }
            }
            R.id.quiz_close_btn -> DialogsUtil.showExitQuizDialog(
                requireContext(),
                navController,
                binding.normalCountDownView
            )
        }
    }

    private fun submitResult() {
        // show loading dialog
        DialogsUtil.showLoadingDialog(requireContext())
        // calculate progress
        val totalMarks: Float = randomQueList.size * quizData.correctAnsMarks
        val marksScored: Float =
            (correctAnswer * quizData.correctAnsMarks) - (wrongAnswer * (-quizData.wrongAnsMarks)) // ///// negative marking lgani hai abhi
        val marksPercent: Float = (marksScored / totalMarks) * 100

        val myResult = MyResult(
            quizName = quizName,
            heldOn = quizData.quizStartDate,
            user = user,
            correctAns = correctAnswer,
            wrongAns = wrongAnswer,
            missedAns = randomQueList.size - correctAnswer - wrongAnswer,
            scoredPercent = marksPercent,
            marksScored = marksScored,
            totalMarks = totalMarks
        )
        viewModel.setResult(myResult)
        viewModel.uploadResult(quizId, myResult)
        viewModel.updateParticipationStatus(quizData.quiz_id)
        DialogsUtil.dismissDialog()

//                    Check currentDestination before calling navigate might be helpful....
//                    For example, if you have two fragment destinations on the navigation graph fragmentA and fragmentB,
//                    and there is only one action from fragmentA to fragmentB. calling navigate(R.id.action_fragmentA_to_fragmentB)
//                    will result in IllegalArgumentException when you were already on fragmentB.
//                    Therefor you should always check the currentDestination before navigating.
//
//                    if (navController.currentDestination?.id == R.id.fragmentA) {
//                        navController.navigate(R.id.action_fragmentA_to_fragmentB)
//                    }
    }

    private fun resetOptions() = binding.apply {
        quizOptionOne.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        quizOptionTwo.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        quizOptionThree.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        quizOptionFour.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        quizOptionOne.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorLightText))
        quizOptionTwo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorLightText))
        quizOptionThree.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLightText
            )
        )
        quizOptionFour.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLightText
            )
        )
    }

    private fun verifyAnswer(selectedButton: Button) {
        if (canAnswer) {
            selectedButton.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.new_btn_bg)
            selectedButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_text
                )
            )
            if (randomQueList[currentQuesNum - 1].answer == selectedButton.text) correctAnswer++ else wrongAnswer++
            canAnswer = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // my POJO class for retrieving quizList
    @Keep
    data class QuestionsListModel(val questionsList: ArrayList<QuestionsModel>? = null)
}

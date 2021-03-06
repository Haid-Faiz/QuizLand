package com.example.quiz_app_mvvm.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
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
    private lateinit var _binding: FragmentQuizBinding
    private val quizViewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        requireActivity().onBackPressedDispatcher.addCallback(this@QuizFragment) {
            DialogsUtil.showExitQuizDialog(
                requireContext(),
                navController,
                _binding.normalCountDownView
            )
        }

        val args: QuizFragmentArgs by navArgs()
        quizId = args.quizDocumentID
        totalQuestions = args.totalQuestions
        quizName = args.quizName
//        quizId = QuizFragmentArgs.fromBundle(getArguments).quizDocumentID
//        totalQuestions = QuizFragmentArgs.fromBundle(getArguments).totalQuestions
//        quizName = QuizFragmentArgs.fromBundle(getArguments).quizName
        // ---------------------------------------------
        quizData = quizViewModel.getQuizData()
        // --------------------------------------
        quizViewModel.getQuestions(
            userId = Firebase.auth.currentUser?.uid!!,
            quizID = quizData.quiz_id
        )
        quizViewModel.questions.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Error -> showSnackBar(it.message!!)
                is Resource.Loading -> TODO()
                is Resource.Success -> {
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

        _binding.quizOptionOne.setOnClickListener(this)
        _binding.quizOptionTwo.setOnClickListener(this)
        _binding.quizOptionThree.setOnClickListener(this)
        _binding.quizOptionFour.setOnClickListener(this)
        _binding.quizNextBtn.setOnClickListener(this)
        _binding.quizCloseBtn.setOnClickListener(this)
    }

    private fun pickQuestion(questionList: ArrayList<QuestionsModel>?) {
        for (i in questionList?.indices!!) {
            val randomNum = Random().nextInt(questionList.size) // getRandomNum(questionList.size)
            randomQueList.add(questionList[randomNum])
            questionList.removeAt(randomNum)
        }
    }

    private fun loadUI() {
        _binding.quizTitle.text = quizName
        enableOptions()
        loadQuestion(1) // question no. 1
        startTimer()
    }

    private fun enableOptions() {
        // show all options button
        _binding.quizOptionOne.isVisible = true
        _binding.quizOptionTwo.isVisible = true
        _binding.quizOptionThree.isVisible = true
        _binding.quizOptionFour.isVisible = true

        // enable options button
        _binding.quizOptionOne.isEnabled = true
        _binding.quizOptionTwo.isEnabled = true
        _binding.quizOptionThree.isEnabled = true
        _binding.quizOptionFour.isEnabled = true
        // show next button
        _binding.quizNextBtn.isVisible = true
    }

    private fun loadQuestion(questionSerialNum: Int) {
        _binding.quizQuestionNum.text =
            "Q.No. $questionSerialNum out of ${randomQueList.size}"
        // load question text
        _binding.quizQuestion.text = randomQueList[questionSerialNum - 1].question
        // load option button
        _binding.quizOptionOne.text = randomQueList[questionSerialNum - 1].option_a
        _binding.quizOptionTwo.text = randomQueList[questionSerialNum - 1].option_b
        _binding.quizOptionThree.text = randomQueList[questionSerialNum - 1].option_c
        _binding.quizOptionFour.text = randomQueList[questionSerialNum - 1].option_d

        if (questionSerialNum == randomQueList.size) {
            _binding.quizNextBtn.text = "Submit Quiz"
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
        _binding.normalCountDownView.initTimer(remainingTime)
        // set OnTickListener for getting updates on time.
        _binding.normalCountDownView.setOnTickListener(object : HappyTimer.OnTickListener {

            override fun onTick(completedSeconds: Int, remainingSeconds: Int) {}

            override fun onTimeUp() {
                canAnswer = false
                submitResult()
                DialogsUtil.showTimeUpDialog(requireContext()) {
                    navController.navigate(R.id.action_quizFragment_to_resultFragment)
                }
            }
        })
        _binding.normalCountDownView.startTimer()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.quiz_option_one -> verifyAnswer(_binding.quizOptionOne)
            R.id.quiz_option_two -> verifyAnswer(_binding.quizOptionTwo)
            R.id.quiz_option_three -> verifyAnswer(_binding.quizOptionThree)
            R.id.quiz_option_four -> verifyAnswer(_binding.quizOptionFour)
            R.id.quiz_next_btn -> {
                if (currentQuesNum == randomQueList.size) {
                    // submit results
                    _binding.normalCountDownView.stopTimer() // Saved memory leak
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
                _binding.normalCountDownView
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
        quizViewModel.setResult(myResult)
        quizViewModel.uploadResult(quizId, myResult)
        quizViewModel.updateParticipationStatus(quizData.quiz_id)
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

    private fun resetOptions() {
        _binding.quizOptionOne.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        _binding.quizOptionTwo.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        _binding.quizOptionThree.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        _binding.quizOptionFour.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.outline_light_btn_bg)
        _binding.quizOptionOne.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLightText
            )
        )
        _binding.quizOptionTwo.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLightText
            )
        )
        _binding.quizOptionThree.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLightText
            )
        )
        _binding.quizOptionFour.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorLightText
            )
        )
    }

    private fun verifyAnswer(selectedButton: Button) {
        if (canAnswer) {
            selectedButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.new_btn_bg)
            selectedButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_text))
            if (randomQueList[currentQuesNum - 1].answer == selectedButton.text) correctAnswer++ else wrongAnswer++
            canAnswer = false
        }
    }

    // my POJO class for retrieving quizList
    data class QuestionsListModel(val questionsList: ArrayList<QuestionsModel>? = null)
}

package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.androchef.happytimer.countdowntimer.HappyTimer
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.databinding.FragmentQuizBinding
import com.example.quiz_app_mvvm.model.MyResult
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.model.User
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import java.util.*
import kotlin.collections.ArrayList

class QuizFragment : Fragment(), View.OnClickListener {
    // UI elements
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
    private lateinit var fragmentQuizBinding: FragmentQuizBinding
//    private val viewModel: QuizListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentQuizBinding = FragmentQuizBinding.inflate(inflater, container, false)
        return fragmentQuizBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(this@QuizFragment) {
            DialogsUtil.showExitQuizDialog(
                    requireContext(),
                    navController,
                    fragmentQuizBinding.normalCountDownView)
        }

        val args: QuizFragmentArgs by navArgs()
        quizId = args.quizDocumentID
        totalQuestions = args.totalQuestions
        quizName = args.quizName
//        quizId = QuizFragmentArgs.fromBundle(getArguments).quizDocumentID
//        totalQuestions = QuizFragmentArgs.fromBundle(getArguments).totalQuestions
//        quizName = QuizFragmentArgs.fromBundle(getArguments).quizName
        // ---------------------------------------------
        val viewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quizData = viewModel.getQuizData()
        // --------------------------------------

        val quizDao = QuizDao()
        quizDao.userCollection
                .document(quizDao.user?.uid!!)
                .collection("MyParticipatedQuiz")
                .document(quizData.quiz_id)
                .collection("Questions")
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        val questionListModel: QuestionsListModel? = it.result?.documents?.get(0)?.toObject(QuestionsListModel::class.java)

//                        val querySnapshot: QuerySnapshot? = it.result
//                        val a: MutableList<DocumentSnapshot>? = querySnapshot?.documents
//                        val ds: DocumentSnapshot? = a?.get(0)
//                        val questionListModel: QuestionsListModel? = ds?.toObject(QuestionsListModel::class.java)
                        val questionList: ArrayList<QuestionsModel>? = questionListModel?.questionsList

                        pickQuestion(questionList)
                        loadUI()
                    } else {
                        // set the error
                        fragmentQuizBinding.quizTitle.text = "Error loading in data..."
                    }
                }

        fragmentQuizBinding.quizOptionOne.setOnClickListener(this)
        fragmentQuizBinding.quizOptionTwo.setOnClickListener(this)
        fragmentQuizBinding.quizOptionThree.setOnClickListener(this)
        fragmentQuizBinding.quizOptionFour.setOnClickListener(this)
        fragmentQuizBinding.quizNextBtn.setOnClickListener(this)
        fragmentQuizBinding.quizCloseBtn.setOnClickListener(this)
    }

    private fun pickQuestion(questionList: ArrayList<QuestionsModel>?) {
        for (i in questionList?.indices!!) {
            val randomNum = Random().nextInt(questionList.size)   //getRandomNum(questionList.size)
            randomQueList.add(questionList[randomNum])
            questionList.removeAt(randomNum)
        }
    }

    private fun loadUI() {
        fragmentQuizBinding.quizTitle.text = quizName
        // Enable options
        enableOptions()
        loadQuestion(1)          // question no. 1
        // start timer
        startTimer()
    }

    private fun enableOptions() {
        // show all options button
        fragmentQuizBinding.quizOptionOne.visibility = View.VISIBLE
        fragmentQuizBinding.quizOptionTwo.visibility = View.VISIBLE
        fragmentQuizBinding.quizOptionThree.visibility = View.VISIBLE
        fragmentQuizBinding.quizOptionFour.visibility = View.VISIBLE

        // enable options button
        fragmentQuizBinding.quizOptionOne.isEnabled = true
        fragmentQuizBinding.quizOptionTwo.isEnabled = true
        fragmentQuizBinding.quizOptionThree.isEnabled = true
        fragmentQuizBinding.quizOptionFour.isEnabled = true
        // show next button
        fragmentQuizBinding.quizNextBtn.visibility = View.VISIBLE
    }

    private fun loadQuestion(questionSerialNum: Int) {
        fragmentQuizBinding.quizQuestionNum.text = "Q.No. ${questionSerialNum.toString()} out of ${randomQueList.size}"
        // load question text
        fragmentQuizBinding.quizQuestion.text = randomQueList[questionSerialNum - 1].question
        // load option button
        fragmentQuizBinding.quizOptionOne.text = randomQueList[questionSerialNum - 1].option_a
        fragmentQuizBinding.quizOptionTwo.text = randomQueList[questionSerialNum - 1].option_b
        fragmentQuizBinding.quizOptionThree.text = randomQueList[questionSerialNum - 1].option_c
        fragmentQuizBinding.quizOptionFour.text = randomQueList[questionSerialNum - 1].option_d

        if (questionSerialNum == randomQueList.size) {
            fragmentQuizBinding.quizNextBtn.text = "Submit Quiz"
        }
        // question loaded, now user can ans the question
        canAnswer = true
        currentQuesNum = questionSerialNum
    }

    private fun startTimer() {
        // setting time duration
        val endTimeInSeconds = (quizData.quizStartDate!!.quizStartTimeHour + quizData.quizDurationHour) * 3600 + (quizData.quizStartDate!!.quizStartTimeMin + quizData.quizDurationMin) * 60

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMin = calendar.get(Calendar.MINUTE)
        val currentSec = calendar.get(Calendar.SECOND)

        val currentTimeInSeconds = currentHour * 3600 + currentMin * 60 + currentSec
        val remainingTime = endTimeInSeconds - currentTimeInSeconds

        // Initializing Timer with seconds
        fragmentQuizBinding.normalCountDownView.initTimer(remainingTime)
        // set OnTickListener for getting updates on time.
        fragmentQuizBinding.normalCountDownView.setOnTickListener(object : HappyTimer.OnTickListener {

            override fun onTick(completedSeconds: Int, remainingSeconds: Int) {}

            override fun onTimeUp() {
                canAnswer = false
                submitResult()
                DialogsUtil.showTimeUpDialog(requireContext()) {
                    navController.navigate(R.id.action_quizFragment_to_resultFragment)
                }
            }
        })
        fragmentQuizBinding.normalCountDownView.startTimer()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.quiz_option_one -> verifyAnswer(fragmentQuizBinding.quizOptionOne)
            R.id.quiz_option_two -> verifyAnswer(fragmentQuizBinding.quizOptionTwo)
            R.id.quiz_option_three -> verifyAnswer(fragmentQuizBinding.quizOptionThree)
            R.id.quiz_option_four -> verifyAnswer(fragmentQuizBinding.quizOptionFour)
            R.id.quiz_next_btn -> {
                if (currentQuesNum == randomQueList.size) {
                    // submit results
                    fragmentQuizBinding.normalCountDownView.stopTimer()    // Saved memory leak
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
                    fragmentQuizBinding.normalCountDownView)
        }
    }

    private fun submitResult() {
        // show loading dialog
        DialogsUtil.showLoadingDialog(requireContext())

        val quizDao = QuizDao()
        val user = User(
                quizDao.user?.uid!!,
                quizDao.user.displayName,
                quizDao.user.photoUrl.toString()
        )
        // calculate progress
        val totalMarks: Float = randomQueList.size * quizData.correctAnsMarks
        val marksScored: Float = (correctAnswer * quizData.correctAnsMarks) - (wrongAnswer * (-quizData.wrongAnsMarks))  /////// negative marking lgani hai abhi
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
        val viewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        viewModel.setResult(myResult)
        quizDao.setResult(quizId, myResult)
        quizDao.updateParticipation(quizData.quiz_id)

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
        fragmentQuizBinding.quizOptionOne.background = resources.getDrawable(R.drawable.outline_light_btn_bg)
        fragmentQuizBinding.quizOptionTwo.background = resources.getDrawable(R.drawable.outline_light_btn_bg)
        fragmentQuizBinding.quizOptionThree.background = resources.getDrawable(R.drawable.outline_light_btn_bg)
        fragmentQuizBinding.quizOptionFour.background = resources.getDrawable(R.drawable.outline_light_btn_bg)
        fragmentQuizBinding.quizOptionOne.setTextColor(resources.getColor(R.color.colorLightText))
        fragmentQuizBinding.quizOptionTwo.setTextColor(resources.getColor(R.color.colorLightText))
        fragmentQuizBinding.quizOptionThree.setTextColor(resources.getColor(R.color.colorLightText))
        fragmentQuizBinding.quizOptionFour.setTextColor(resources.getColor(R.color.colorLightText))
    }

    private fun verifyAnswer(selectedButton: Button) {
        if (canAnswer) {
            selectedButton.background = resources.getDrawable(R.drawable.new_btn_bg)
            selectedButton.setTextColor(resources.getColor(R.color.my_purple))
            if (randomQueList[currentQuesNum - 1].answer == selectedButton.text) correctAnswer++ else wrongAnswer++
            canAnswer = false
        }
    }

    // my POJO class for retrieving quizList
    data class QuestionsListModel(val questionsList: ArrayList<QuestionsModel>? = null)
}
package com.example.quiz_app_mvvm.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.utilities.DialogsUtil
import com.example.quiz_app_mvvm.daos.QuizDao
import com.example.quiz_app_mvvm.databinding.FragmentAddQuestBinding
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import com.google.android.material.snackbar.Snackbar

class AddQuestFragment : Fragment(), QuizDao.UploadedCallBack, CompoundButton.OnCheckedChangeListener {

    private lateinit var fragmentAddQuestBinding: FragmentAddQuestBinding
    private lateinit var quizModel: QuizModel
    private lateinit var navController: NavController
    private var currentQuestNum: Int = 1
    private var questionsList = ArrayList<QuestionsModel>()
    private lateinit var popUpAnim: Animation


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        fragmentAddQuestBinding = FragmentAddQuestBinding.inflate(inflater, container, false)
        return fragmentAddQuestBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        fragmentAddQuestBinding.questNum.text = currentQuestNum.toString()
        popUpAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_in)

        val quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)
        quizModel = quizListViewModel.getQuizData()

        if (currentQuestNum == quizModel.questions)
            fragmentAddQuestBinding.nextQuestionBtn.text = "Submit quiz"

        // Adding clickListeners to switches
        fragmentAddQuestBinding.switch1.setOnCheckedChangeListener(this)
        fragmentAddQuestBinding.switch2.setOnCheckedChangeListener(this)
        fragmentAddQuestBinding.switch3.setOnCheckedChangeListener(this)
        fragmentAddQuestBinding.switch4.setOnCheckedChangeListener(this)

        fragmentAddQuestBinding.discardQestionBtn.setOnClickListener {
            navController.popBackStack()
        }

        fragmentAddQuestBinding.nextQuestionBtn.setOnClickListener {
            val question = fragmentAddQuestBinding.enterQuestion.text.toString().trim()
            val optionOne = fragmentAddQuestBinding.enterOptionOne.text.toString().trim()
            val optionTwo = fragmentAddQuestBinding.enterOptionSecond.text.toString().trim()
            val optionThree = fragmentAddQuestBinding.enterOptionThree.text.toString().trim()
            val optionFourth = fragmentAddQuestBinding.enterOptionFourth.text.toString().trim()

            var answer = ""
            val isAnswerMarked: Boolean = when {
                fragmentAddQuestBinding.switch1.isChecked -> {
                    answer = fragmentAddQuestBinding.enterOptionOne.text.toString().trim()
                    true
                }
                fragmentAddQuestBinding.switch2.isChecked -> {
                    answer = fragmentAddQuestBinding.enterOptionSecond.text.toString().trim()
                    true
                }
                fragmentAddQuestBinding.switch3.isChecked -> {
                    answer = fragmentAddQuestBinding.enterOptionThree.text.toString().trim()
                    true
                }
                fragmentAddQuestBinding.switch4.isChecked -> {
                    answer = fragmentAddQuestBinding.enterOptionFourth.text.toString().trim()
                    true
                }
                else -> false
            }

            if (question.isNotEmpty() && isAnswerMarked && optionOne.isNotEmpty() && optionTwo.isNotEmpty()
                    && optionThree.isNotEmpty() && optionFourth.isNotEmpty()) {

                val questionsModel = QuestionsModel(
                        question = question,
                        answer = answer,
                        option_a = optionOne,
                        option_b = optionTwo,
                        option_c = optionThree,
                        option_d = optionFourth,
                )
                questionsList.add(questionsModel)

                if (currentQuestNum == quizModel.questions) {
                    submitQuiz(quizModel)
                } else {
                    currentQuestNum++
                    resetOption()
                    if (currentQuestNum == quizModel.questions)
                        fragmentAddQuestBinding.nextQuestionBtn.text = "Submit quiz"
                }

            } else {

                if (!isAnswerMarked)
                    Snackbar.make(fragmentAddQuestBinding.root, "Please mark any option as an answer", Snackbar.LENGTH_LONG).show()

                if (question.isNotEmpty())
                    fragmentAddQuestBinding.enterQuestion.error = null
                else
                    fragmentAddQuestBinding.enterQuestion.error = "Required"

                if (optionOne.isNotEmpty())
                    fragmentAddQuestBinding.enterOptionOne.error = null
                else
                    fragmentAddQuestBinding.enterOptionOne.error = "Required"

                if (optionTwo.isNotEmpty())
                    fragmentAddQuestBinding.enterOptionSecond.error = null
                else
                    fragmentAddQuestBinding.enterOptionSecond.error = "Required"

                if (optionThree.isNotEmpty())
                    fragmentAddQuestBinding.enterOptionThree.error = null
                else
                    fragmentAddQuestBinding.enterOptionThree.error = "Required"

                if (optionFourth.isNotEmpty())
                    fragmentAddQuestBinding.enterOptionFourth.error = null
                else
                    fragmentAddQuestBinding.enterOptionFourth.error = "Required"
            }
        }
    }

    private fun submitQuiz(quizModel: QuizModel) {
//        fragmentAddQuestBinding.pb.visibility = View.VISIBLE
        DialogsUtil.showLoadingDialog(requireContext())
        val quizDao = QuizDao(this)
        quizDao.createQuiz(quizModel, questionsList)
    }

    override fun isUploaded(isAdded: Boolean, docID: String) {

//        fragmentAddQuestBinding.pb.visibility = View.INVISIBLE
        DialogsUtil.dismissDialog()

        if (isAdded) {

            Snackbar.make(fragmentAddQuestBinding.root, "Your quiz is Successfully uploaded", Snackbar.LENGTH_LONG).show()
            DialogsUtil.showShareIDDialog(requireContext(), docID, activity)
            navController.navigate(R.id.action_addQuestFragment_to_createdQuizesFragment)

        } else {
            Snackbar.make(fragmentAddQuestBinding.root, "Something went wrong...", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun isDeleted(isDeleted: Boolean) {
//        TODO("Not yet implemented")
    }

    private fun resetOption() {
        fragmentAddQuestBinding.enterQuestion.text.clear()
        fragmentAddQuestBinding.enterOptionOne.text.clear()
        fragmentAddQuestBinding.enterOptionSecond.text.clear()
        fragmentAddQuestBinding.enterOptionThree.text.clear()
        fragmentAddQuestBinding.enterOptionFourth.text.clear()
        fragmentAddQuestBinding.questNum.text = currentQuestNum.toString()

        // reset switch buttons
        fragmentAddQuestBinding.switch1.isChecked = false
        fragmentAddQuestBinding.switch2.isChecked = false
        fragmentAddQuestBinding.switch3.isChecked = false
        fragmentAddQuestBinding.switch4.isChecked = false
        fragmentAddQuestBinding.switch1.visibility = View.VISIBLE
        fragmentAddQuestBinding.switch2.visibility = View.VISIBLE
        fragmentAddQuestBinding.switch3.visibility = View.VISIBLE
        fragmentAddQuestBinding.switch4.visibility = View.VISIBLE
    }

    override fun onCheckedChanged(p0: CompoundButton, p1: Boolean) {

        when (p0.id) {
            R.id.switch1 -> {
                if (p0.isChecked) {
                    fragmentAddQuestBinding.switch1.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.INVISIBLE
                    // lottie
                    fragmentAddQuestBinding.correctOptionOneAnim.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionOneAnim.playAnimation()
                    fragmentAddQuestBinding.checkMarkOne.startAnimation(popUpAnim)
                } else {
                    fragmentAddQuestBinding.switch1.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionOneAnim.visibility = View.INVISIBLE
                }
            }

            R.id.switch2 -> {
                if (p0.isChecked) {
                    fragmentAddQuestBinding.switch1.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.correctOptionSecondAnim.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionSecondAnim.playAnimation()
                } else {
                    fragmentAddQuestBinding.switch1.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionSecondAnim.visibility = View.INVISIBLE
                }
            }
            R.id.switch3 -> {

                if (p0.isChecked) {
                    fragmentAddQuestBinding.switch1.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.correctOptionThreeAnim.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionThreeAnim.playAnimation()
                } else {
                    fragmentAddQuestBinding.switch1.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionThreeAnim.visibility = View.INVISIBLE
                }
            }
            R.id.switch4 -> {

                if (p0.isChecked) {
                    fragmentAddQuestBinding.switch1.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.INVISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionFourthAnim.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionFourthAnim.playAnimation()
                } else {
                    fragmentAddQuestBinding.switch1.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch2.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch3.visibility = View.VISIBLE
                    fragmentAddQuestBinding.switch4.visibility = View.VISIBLE
                    fragmentAddQuestBinding.correctOptionFourthAnim.visibility = View.INVISIBLE
                }
            }
        }
    }
}
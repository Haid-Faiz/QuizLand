package com.example.quiz_app_mvvm.ui.quiz.create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentAddQuestBinding
import com.example.quiz_app_mvvm.model.QuestionsModel
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.DialogsUtil
import com.example.quiz_app_mvvm.util.Resource
import com.example.quiz_app_mvvm.util.showSnackBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddQuestFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    private lateinit var _binding: FragmentAddQuestBinding
    private lateinit var quizModel: QuizModel
    private lateinit var navController: NavController
    private var currentQuestNum: Int = 1
    private var questionsList = ArrayList<QuestionsModel>()
    private lateinit var popUpAnim: Animation
    private lateinit var popDownAnim: Animation
    private val quizViewModel: QuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddQuestBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        _binding.questNum.text = currentQuestNum.toString()
        popUpAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_in)
        popDownAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_out)
//        val quizViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)
        quizModel = quizViewModel.getQuizData()

        if (currentQuestNum == quizModel.questions)
            _binding.nextQuestionBtn.text = "Submit quiz"

        // Adding clickListeners to switches
        _binding.switch1.setOnCheckedChangeListener(this)
        _binding.switch2.setOnCheckedChangeListener(this)
        _binding.switch3.setOnCheckedChangeListener(this)
        _binding.switch4.setOnCheckedChangeListener(this)
        _binding.discardQestionBtn.setOnClickListener { navController.popBackStack() }
        _binding.nextQuestionBtn.setOnClickListener {
            val question = _binding.enterQuestion.text.toString().trim()
            val optionOne = _binding.enterOptionOne.text.toString().trim()
            val optionTwo = _binding.enterOptionSecond.text.toString().trim()
            val optionThree = _binding.enterOptionThree.text.toString().trim()
            val optionFourth = _binding.enterOptionFourth.text.toString().trim()

            var answer = ""
            val isAnswerMarked: Boolean = when {
                _binding.switch1.isChecked -> {
                    answer = _binding.enterOptionOne.text.toString().trim()
                    true
                }
                _binding.switch2.isChecked -> {
                    answer = _binding.enterOptionSecond.text.toString().trim()
                    true
                }
                _binding.switch3.isChecked -> {
                    answer = _binding.enterOptionThree.text.toString().trim()
                    true
                }
                _binding.switch4.isChecked -> {
                    answer = _binding.enterOptionFourth.text.toString().trim()
                    true
                }
                else -> false
            }

            if (question.isNotEmpty() && isAnswerMarked && optionOne.isNotEmpty() && optionTwo.isNotEmpty() &&
                optionThree.isNotEmpty() && optionFourth.isNotEmpty()
            ) {

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
                        _binding.nextQuestionBtn.text = "Submit quiz"
                }
            } else {

                if (!isAnswerMarked)
                    Snackbar.make(
                        _binding.root,
                        "Please mark any option as an answer",
                        Snackbar.LENGTH_LONG
                    ).show()

                if (question.isNotEmpty())
                    _binding.enterQuestion.error = null
                else
                    _binding.enterQuestion.error = "Required"

                if (optionOne.isNotEmpty())
                    _binding.enterOptionOne.error = null
                else
                    _binding.enterOptionOne.error = "Required"

                if (optionTwo.isNotEmpty())
                    _binding.enterOptionSecond.error = null
                else
                    _binding.enterOptionSecond.error = "Required"

                if (optionThree.isNotEmpty())
                    _binding.enterOptionThree.error = null
                else
                    _binding.enterOptionThree.error = "Required"

                if (optionFourth.isNotEmpty())
                    _binding.enterOptionFourth.error = null
                else
                    _binding.enterOptionFourth.error = "Required"
            }
        }
    }

    private fun submitQuiz(quizModel: QuizModel) {
        closeKeyboard()
        // switch bottom bar to account
        DialogsUtil.showLoadingDialog(requireContext())
        lifecycleScope.launchWhenCreated {
            quizViewModel.createQuiz(quizModel, questionsList).let {
                when (it) {
                    is Resource.Error -> showSnackBar("Something went wrong")
                    is Resource.Loading -> TODO()
                    is Resource.Success -> {
                        DialogsUtil.dismissDialog()
                        showSnackBar(message = "Your quiz is Successfully uploaded")
//                    DialogsUtil.showShareIDDialog(requireContext(), docID, activity)
                        navController.navigate(R.id.action_addQuestFragment_to_createdQuizesFragment)
                    }
                }
            }
        }
    }

    private fun closeKeyboard() {
        val currentView: View? = requireActivity().currentFocus
        currentView?.let {
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun resetOption() {
        _binding.enterQuestion.text.clear()
        _binding.enterOptionOne.text.clear()
        _binding.enterOptionSecond.text.clear()
        _binding.enterOptionThree.text.clear()
        _binding.enterOptionFourth.text.clear()
        _binding.questNum.text = currentQuestNum.toString()

        // reset switch buttons
        _binding.switch1.isChecked = false
        _binding.switch2.isChecked = false
        _binding.switch3.isChecked = false
        _binding.switch4.isChecked = false
        _binding.switch1.visibility = View.VISIBLE
        _binding.switch2.visibility = View.VISIBLE
        _binding.switch3.visibility = View.VISIBLE
        _binding.switch4.visibility = View.VISIBLE
    }

    override fun onCheckedChanged(p0: CompoundButton, p1: Boolean) {

        when (p0.id) {
            R.id.switch1 -> {
                if (p0.isChecked) {
                    _binding.switch1.visibility = View.VISIBLE
                    _binding.switch2.visibility = View.INVISIBLE
                    _binding.switch3.visibility = View.INVISIBLE
                    _binding.switch4.visibility = View.INVISIBLE
                    // lottie
                    _binding.correctOptionOneAnim.visibility = View.VISIBLE
                    _binding.correctOptionOneAnim.playAnimation()
//                    fragmentAddQuestBinding.checkMarkOne.startAnimation(popUpAnim)
                } else {
                    _binding.switch1.visibility = View.VISIBLE
                    _binding.switch2.visibility = View.VISIBLE
                    _binding.switch3.visibility = View.VISIBLE
                    _binding.switch4.visibility = View.VISIBLE
                    _binding.correctOptionOneAnim.visibility = View.INVISIBLE
//                    fragmentAddQuestBinding.checkMarkOne.visibility = View.INVISIBLE
                }
            }

            R.id.switch2 -> {
                if (p0.isChecked) {
                    _binding.switch1.visibility = View.INVISIBLE
                    _binding.switch2.visibility = View.VISIBLE
                    _binding.switch3.visibility = View.INVISIBLE
                    _binding.switch4.visibility = View.INVISIBLE
                    _binding.correctOptionSecondAnim.visibility = View.VISIBLE
                    _binding.correctOptionSecondAnim.playAnimation()
                } else {
                    _binding.switch1.visibility = View.VISIBLE
                    _binding.switch2.visibility = View.VISIBLE
                    _binding.switch3.visibility = View.VISIBLE
                    _binding.switch4.visibility = View.VISIBLE
                    _binding.correctOptionSecondAnim.visibility = View.INVISIBLE
                }
            }
            R.id.switch3 -> {

                if (p0.isChecked) {
                    _binding.switch1.visibility = View.INVISIBLE
                    _binding.switch2.visibility = View.INVISIBLE
                    _binding.switch3.visibility = View.VISIBLE
                    _binding.switch4.visibility = View.INVISIBLE
                    _binding.correctOptionThreeAnim.visibility = View.VISIBLE
                    _binding.correctOptionThreeAnim.playAnimation()
                } else {
                    _binding.switch1.visibility = View.VISIBLE
                    _binding.switch2.visibility = View.VISIBLE
                    _binding.switch3.visibility = View.VISIBLE
                    _binding.switch4.visibility = View.VISIBLE
                    _binding.correctOptionThreeAnim.visibility = View.INVISIBLE
                }
            }
            R.id.switch4 -> {

                if (p0.isChecked) {
                    _binding.switch1.visibility = View.INVISIBLE
                    _binding.switch2.visibility = View.INVISIBLE
                    _binding.switch3.visibility = View.INVISIBLE
                    _binding.switch4.visibility = View.VISIBLE
                    _binding.correctOptionFourthAnim.visibility = View.VISIBLE
                    _binding.correctOptionFourthAnim.playAnimation()
                } else {
                    _binding.switch1.visibility = View.VISIBLE
                    _binding.switch2.visibility = View.VISIBLE
                    _binding.switch3.visibility = View.VISIBLE
                    _binding.switch4.visibility = View.VISIBLE
                    _binding.correctOptionFourthAnim.visibility = View.INVISIBLE
                }
            }
        }
    }
}

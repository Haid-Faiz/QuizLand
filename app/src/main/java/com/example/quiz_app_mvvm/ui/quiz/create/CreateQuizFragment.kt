package com.example.quiz_app_mvvm.ui.quiz.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentCreateQuizBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*

class CreateQuizFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var _binding: FragmentCreateQuizBinding
    private var hourDuration: Int = 0
    private var minDuration: Int = 0
    private var difficultyLevel: String = ""
    private val GALLERY_REQUEST_CODE: Int = 12
    private var imageUri: String = ""
    private lateinit var pickedDate: QuizModel.MyDate
    private var isClicked: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isClicked = false
        navController = Navigation.findNavController(view)
        pickedDate = QuizModel.MyDate()

        val quizListViewModel = ViewModelProvider(requireActivity()).get(QuizViewModel::class.java)

        _binding.quizDurationMinPicker.minValue = 0
        _binding.quizDurationMinPicker.maxValue = 59
        _binding.quizDurationHourPicker.minValue = 0
        _binding.quizDurationHourPicker.maxValue = 23

        _binding.discardQuizBtn.setOnClickListener {
            navController.navigateUp()
        }

        _binding.quizDurationHourPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(numberPicker: NumberPicker?, oldValue: Int, newValue: Int) {
                hourDuration = newValue
            }
        })

        _binding.quizDurationMinPicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            minDuration = newValue
        }

        _binding.radioGrp.setOnCheckedChangeListener { radioGroup, selectedButtonid ->
            when (selectedButtonid) {
                R.id.radio_btn_beginner_level -> difficultyLevel = "Beginner"
                R.id.intermediate_level_radio_btn -> difficultyLevel = "Intermediate"
                R.id.advanced_level_radio_btn -> difficultyLevel = "Advance"
            }
        }

        _binding.addQuestionBtn.setOnClickListener {

            // taking inputs from user
            val quizName = _binding.enterQuizName.text.toString().trim()
            val quizDesc = _binding.enterQuizDescription.text.toString().trim()
            val totalQuestNumString = _binding.enterQuestNum.text.toString().trim()
            val correctAnsMarks = _binding.enterCorrectAnsMarks.text.toString().trim()
            val wrongAnsMarks = _binding.enterWrongAnsMarks.text.toString().trim()
            val quizCreatedBy = _binding.enterQuizCreatedBy.text.toString().trim()

            val isQuizDurationSet = _binding.quizDurationMinPicker.value != 0 ||
                    _binding.quizDurationHourPicker.value != 0

            if (quizName.isNotEmpty() && quizDesc.isNotEmpty() && difficultyLevel.isNotEmpty()
                    && totalQuestNumString.isNotEmpty() && quizCreatedBy.isNotEmpty() &&
                    correctAnsMarks.isNotEmpty() && wrongAnsMarks.isNotEmpty()
                    && isClicked && isQuizDurationSet && wrongAnsMarks.toFloat() <= 0) {

                // creating quiz object
                val quizModel = QuizModel(
                        name = quizName,
                        description = quizDesc,
                        level = difficultyLevel,
                        questions = totalQuestNumString.toInt(),
                        createdBy = quizCreatedBy,
                        createdAt = System.currentTimeMillis(),
                        quizStartDate = pickedDate,
                        imageUrl = imageUri,
                        visibility = "public",
                        quizDurationHour = hourDuration,
                        quizDurationMin = minDuration,
                        correctAnsMarks = correctAnsMarks.toFloat(),
                        wrongAnsMarks = wrongAnsMarks.toFloat()
                )

                closeKeyBoard()
                quizListViewModel.setQuizData(quizModel)    // sending data to next fragment with help of viewmodel
                navController.navigate(R.id.action_createQuizFragment_to_addQuestFragment)

            } else {
                if (isClicked)
                    _binding.quizStartTime.error = null
                else
                    _binding.quizStartTime.error = "Required"

                if (isQuizDurationSet)
                    _binding.selectDurationHint.error = null
                else
                    _binding.selectDurationHint.error = "Required"

                if (quizName.isNotEmpty())
                    _binding.enterQuizName.error = null
                else
                    _binding.enterQuizName.error = "Required"

                if (quizDesc.isNotEmpty())
                    _binding.enterQuizDescription.error = null
                else
                    _binding.enterQuizDescription.error = "Required"

                if (difficultyLevel.isNotEmpty())
                    _binding.selectDifficulty.error = null
                else
                    _binding.selectDifficulty.error = "Required"

                if (totalQuestNumString.isNotEmpty())
                    _binding.enterQuestNum.error = null
                else
                    _binding.enterQuestNum.error = "Required"

                if (quizCreatedBy.isNotEmpty())
                    _binding.enterQuizCreatedBy.error = null
                else
                    _binding.enterQuizCreatedBy.error = "Required"

                if (correctAnsMarks.isNotEmpty())
                    _binding.enterCorrectAnsMarks.error = null
                else
                    _binding.enterCorrectAnsMarks.error = "Required"

                if (wrongAnsMarks.isNotEmpty()) {
                    _binding.enterWrongAnsMarks.error = null

                    if (wrongAnsMarks.toFloat() > 0) {
                        _binding.enterWrongAnsMarks.error = "It should be zero or negative"
                        Snackbar.make(_binding.root, "Wrong answer marks cannot be greater than zero!", Snackbar.LENGTH_SHORT).show()
                    }
                } else
                    _binding.enterWrongAnsMarks.error = "Required"
            }
        }

        _binding.quizStartTime.setOnClickListener {
            pickDate()
        }

        _binding.addQuizImage.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    private fun closeKeyBoard() {
        val currentView = this.requireView()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentView.windowToken, 0)
    }

    private fun pickDate() {
        // Firstly get calendar object to get current time, so we can pass it in dialog listeners to show this time for first time
        val calendar = Calendar.getInstance()
        val currentYear: Int = calendar.get(Calendar.YEAR)
        val currentMonth: Int = calendar.get(Calendar.MONTH)
        val currentDay: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.DatePickerStyle,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, date: Int) {
                        pickedDate.year = year
                        pickedDate.month = month
                        pickedDate.date = date
                        pickTime()
                    }
                }, currentYear, currentMonth, currentDay
        ).show()  // months are zero based >>>> January -> 0

//        val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
//            // get the values here
//        }, year, month, day)
//        // when we only have one method in interface then we don't need to implement its method by using object keyword
    }

    private fun pickTime() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)  // this will return 24 hours format time
//        val currentHour = calendar.get(Calendar.HOUR)   // this will return normal am pm format time
        val currentMin = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
                requireContext(),
                R.style.DatePickerStyle,
                TimePickerDialog.OnTimeSetListener { timePicker, hour: Int, min: Int ->
                    pickedDate.quizStartTimeHour = hour
                    pickedDate.quizStartTimeMin = min
                    isClicked = true

                    _binding.quizStartTime.text = "${pickedDate.quizStartTimeHour}:${pickedDate.quizStartTimeMin}, ${pickedDate.date}/${pickedDate.month}/${pickedDate.year}"
                },
                currentHour, currentMin, false    // here we can also set that... in which format we want the time
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE) {
            imageUri = data?.data.toString()
            Glide.with(_binding.addQuizImage.context)
                    .load(imageUri)
                    .centerCrop()
                    .into(_binding.addQuizImage)

            if (data != null)
                _binding.lottieAnimImagePlaceholder.visibility = View.GONE
            else
                _binding.lottieAnimImagePlaceholder.visibility = View.VISIBLE
        }
    }

}
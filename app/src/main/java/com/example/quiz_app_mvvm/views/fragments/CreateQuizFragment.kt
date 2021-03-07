package com.example.quiz_app_mvvm.views.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.quiz_app_mvvm.viewmodels.QuizListViewModel
import java.util.*

class CreateQuizFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var fragmentCreateQuizBinding: FragmentCreateQuizBinding
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
        fragmentCreateQuizBinding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        return fragmentCreateQuizBinding.root
//        return inflater.inflate(R.layout.fragment_create_quiz, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        pickedDate = QuizModel.MyDate()

        val quizListViewModel = ViewModelProvider(requireActivity()).get(QuizListViewModel::class.java)

        fragmentCreateQuizBinding.quizDurationMinPicker.minValue = 0
        fragmentCreateQuizBinding.quizDurationMinPicker.maxValue = 59
        fragmentCreateQuizBinding.quizDurationHourPicker.minValue = 0
        fragmentCreateQuizBinding.quizDurationHourPicker.maxValue = 20

        fragmentCreateQuizBinding.discardQuizBtn.setOnClickListener {
            navController.navigateUp()
        }

        fragmentCreateQuizBinding.quizDurationHourPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener {
            override fun onValueChange(numberPicker: NumberPicker?, oldValue: Int, newValue: Int) {
                hourDuration = newValue
            }
        })

        fragmentCreateQuizBinding.quizDurationMinPicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            minDuration = newValue
        }

        fragmentCreateQuizBinding.radioGrp.setOnCheckedChangeListener { radioGroup, selectedButtonid ->
            when (selectedButtonid) {
                R.id.radio_btn_beginner_level -> difficultyLevel = "Beginner"
                R.id.intermediate_level_radio_btn -> difficultyLevel = "Intermediate"
                R.id.advanced_level_radio_btn -> difficultyLevel = "Advance"
            }
        }

        fragmentCreateQuizBinding.addQuestionBtn.setOnClickListener {

            // taking inputs from user
            val quizName = fragmentCreateQuizBinding.enterQuizName.text.toString().trim()
            val quizDesc = fragmentCreateQuizBinding.enterQuizDescription.text.toString().trim()
            val totalQuestNumString = fragmentCreateQuizBinding.enterQuestNum.text.toString().trim()
            val correctAnsMarks = fragmentCreateQuizBinding.enterCorrectAnsMarks.text.toString().trim()
            val wrongAnsMarks = fragmentCreateQuizBinding.enterWrongAnsMarks.text.toString().trim()
            val quizCreatedBy = fragmentCreateQuizBinding.enterQuizCreatedBy.text.toString().trim()

            val isQuizDurationSet = fragmentCreateQuizBinding.quizDurationMinPicker.value != 0 ||
                    fragmentCreateQuizBinding.quizDurationHourPicker.value != 0

            if (quizName.isNotEmpty() && quizDesc.isNotEmpty() && difficultyLevel.isNotEmpty()
                    && totalQuestNumString.isNotEmpty() && quizCreatedBy.isNotEmpty() &&
                    correctAnsMarks.isNotEmpty() && wrongAnsMarks.isNotEmpty()
                    && isClicked && isQuizDurationSet) {

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
                        correctAnsMarks = correctAnsMarks.toLong(),
                        wrongAnsMarks = wrongAnsMarks.toLong()
                )

                quizListViewModel.setQuizData(quizModel)    // sending data to next fragment with help of viewmodel
                navController.navigate(R.id.action_createQuizFragment_to_addQuestFragment)

            } else {

                if (isClicked)
                    fragmentCreateQuizBinding.quizStartTime.error = null
                else
                    fragmentCreateQuizBinding.quizStartTime.error = "Required"

                if (isQuizDurationSet)
                    fragmentCreateQuizBinding.selectDurationHint.error = null
                else
                    fragmentCreateQuizBinding.selectDurationHint.error = "Required"

                if (quizName.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuizName.error = null
                else
                    fragmentCreateQuizBinding.enterQuizName.error = "Required"

                if (quizDesc.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuizDescription.error = null
                else
                    fragmentCreateQuizBinding.enterQuizDescription.error = "Required"

                if (difficultyLevel.isNotEmpty())
                    fragmentCreateQuizBinding.selectDifficulty.error = null
                else
                    fragmentCreateQuizBinding.selectDifficulty.error = "Required"

                if (totalQuestNumString.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuestNum.error = null
                else
                    fragmentCreateQuizBinding.enterQuestNum.error = "Required"

                if (quizCreatedBy.isNotEmpty())
                    fragmentCreateQuizBinding.enterQuizCreatedBy.error = null
                else
                    fragmentCreateQuizBinding.enterQuizCreatedBy.error = "Required"

                if (correctAnsMarks.isNotEmpty())
                    fragmentCreateQuizBinding.enterCorrectAnsMarks.error = null
                else
                    fragmentCreateQuizBinding.enterCorrectAnsMarks.error = "Required"

                if (wrongAnsMarks.isNotEmpty())
                    fragmentCreateQuizBinding.enterWrongAnsMarks.error = null
                else
                    fragmentCreateQuizBinding.enterWrongAnsMarks.error = "Required"
            }
        }

        fragmentCreateQuizBinding.quizStartTime.setOnClickListener {
            pickDate()
        }

        fragmentCreateQuizBinding.addQuizImage.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
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

                    fragmentCreateQuizBinding.quizStartTime.text = "${pickedDate.quizStartTimeHour}:${pickedDate.quizStartTimeMin}, ${pickedDate.date}/${pickedDate.month}/${pickedDate.year}"
                },
                currentHour, currentMin, false    // here we can also set that... in which format we want the time
        ).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE) {
            imageUri = data?.data.toString()
            Glide.with(fragmentCreateQuizBinding.addQuizImage.context)
                    .load(imageUri)
                    .centerCrop()
                    .into(fragmentCreateQuizBinding.addQuizImage)

            if (data != null)
                fragmentCreateQuizBinding.lottieAnimImagePlaceholder.visibility = View.GONE
            else
                fragmentCreateQuizBinding.lottieAnimImagePlaceholder.visibility = View.VISIBLE
        }
    }

}
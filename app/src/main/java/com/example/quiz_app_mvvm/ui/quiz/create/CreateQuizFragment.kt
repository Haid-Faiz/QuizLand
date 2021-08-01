package com.example.quiz_app_mvvm.ui.quiz.create

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.FragmentCreateQuizBinding
import com.example.quiz_app_mvvm.model.QuizModel
import com.example.quiz_app_mvvm.ui.quiz.QuizViewModel
import com.example.quiz_app_mvvm.util.showSnackBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class CreateQuizFragment : Fragment() {

    private val quizViewModel: QuizViewModel by activityViewModels()
    private lateinit var navController: NavController
    private lateinit var _binding: FragmentCreateQuizBinding
    private var hourDuration: Int = 0
    private var minDuration: Int = 0
    private var difficultyLevel: String = ""
    private var imageUri: String = ""
    private lateinit var pickedDate: QuizModel.MyDate
    private var isClicked: Boolean = false
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateQuizBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // if permission is first time allowed, then this will get called
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult.launch(galleryIntent)
            }
        }

        isClicked = false
        navController = Navigation.findNavController(view)
        pickedDate = QuizModel.MyDate()

        _binding.quizDurationMinPicker.minValue = 0
        _binding.quizDurationMinPicker.maxValue = 59
        _binding.quizDurationHourPicker.minValue = 0
        _binding.quizDurationHourPicker.maxValue = 23

        _binding.discardQuizBtn.setOnClickListener { navController.navigateUp() }

        _binding.quizDurationHourPicker.setOnValueChangedListener { numberPicker, oldValue, newValue ->
            hourDuration = newValue
        }

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
                && isClicked && isQuizDurationSet && wrongAnsMarks.toFloat() <= 0
            ) {

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
                quizViewModel.setQuizData(quizModel)    // sending data to next fragment with help of viewmodel
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
                        Snackbar.make(
                            _binding.root,
                            "Wrong answer marks cannot be greater than zero!",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else
                    _binding.enterWrongAnsMarks.error = "Required"
            }
        }

        _binding.quizStartTime.setOnClickListener { pickDate() }

        startActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                // it.data  -> returns an intent
                imageUri = result.data?.data.toString()
                Glide.with(_binding.addQuizImage.context)
                    .load(imageUri)
                    .centerCrop()
                    .into(_binding.addQuizImage)
//                        _binding.addQuizImage.load(imageUri) {
//
//                        }
                if (result.data != null)
                    _binding.lottieAnimImagePlaceholder.visibility = View.GONE
                else
                    _binding.lottieAnimImagePlaceholder.visibility = View.VISIBLE
            }
        }

        _binding.addQuizImage.setOnClickListener {
            // first check permission is accepted or not
            requestPermissions()
        }
    }

    private fun requestPermissions() = when {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED -> {
            // if permission is already granted, then this condition will run
            val galleryIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult.launch(galleryIntent)
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) -> {
            // additional msg, dialog, rationale should be displayed
            showSnackBar(
                "Please allow permission to access photos",
                action = { requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) },
                actionMsg = "Allow"
            )
        }

        else -> {
            // permission hasn't been asked yet. So, request the permission
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun closeKeyBoard() {
        val currentView = this.requireView()
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
            { _, year, month, date ->
                pickedDate.year = year
                pickedDate.month = month
                pickedDate.date = date
                pickTime()
            }, currentYear, currentMonth, currentDay
        ).show()  // months are zero based >>>> January -> 0

//        val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
//            // get the values here
//        }, year, month, day)
//        // when we only have one method in interface then we don't need to implement its method by using object keyword
    }

    private fun pickTime() {
        val calendar = Calendar.getInstance()
        val currentHour =
            calendar.get(Calendar.HOUR_OF_DAY)  // this will return 24 hours format time
//        val currentHour = calendar.get(Calendar.HOUR)   // this will return normal am pm format time
        val currentMin = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.DatePickerStyle,
            { _, hour: Int, min: Int ->
                pickedDate.quizStartTimeHour = hour
                pickedDate.quizStartTimeMin = min
                isClicked = true

                _binding.quizStartTime.text =
                    "${pickedDate.quizStartTimeHour}:${pickedDate.quizStartTimeMin}, ${pickedDate.date}/${pickedDate.month}/${pickedDate.year}"
            },
            currentHour,
            currentMin,
            false    // here we can also set view type of timepicker that... in which format we want time picker
        ).show()
    }
}
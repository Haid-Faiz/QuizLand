package com.example.quiz_app_mvvm.model

import android.widget.TextView
import androidx.databinding.BindingAdapter

data class MyResult(
    var quizName: String = "",
    var user: User? = null,
    var heldOn: QuizModel.MyDate? = null,
    var correctAns: Int = 0,
    var wrongAns: Int = 0,
    var missedAns: Int = 0,
    var scoredPercent: Float = 0F,
    var marksScored: Float = 0F,
    var totalMarks: Float = 0F,
) {

    companion object {

        @BindingAdapter("android:formatFloat")
        @JvmStatic
        fun formatPercent(textView: TextView, floatNum: Float) {
            textView.text = "${String.format("%.1f", floatNum)}%"
        }
    }
}

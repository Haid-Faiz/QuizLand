package com.example.quiz_app_mvvm.model

data class MyResult(
        var quizName: String = "",
        var user: User? = null,
        var heldOn: QuizModel.MyDate? = null,
        var correctAns: Int = 0,
        var wrongAns: Int = 0,
        var missedAns: Int = 0,
        var scoredPercent: Long = 0L,
        var marksScored: Long = 0L,
        var totalMarks: Long = 0L,
)
package com.example.quiz_app_mvvm.model

import androidx.annotation.Keep

@Keep
data class QuestionsModel(
//        @DocumentId
//        val question_id: String = "",
    val question: String = "",
    val answer: String = "",
    val option_a: String = "",
    val option_b: String = "",
    val option_c: String = "",
    val option_d: String = "",
)

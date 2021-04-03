package com.example.quiz_app_mvvm.model

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.quiz_app_mvvm.R
import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.*

data class QuizModel(
        @DocumentId
        var quiz_id: String = "",
        val name: String = "",
        val description: String = "",
        val participated: Boolean = false,
        var imageUrl: String? = "",
        val level: String = "",
        val visibility: String = "",
        val questions: Int = 0,
        val createdBy: String = "",
        val createdAt: Long = 0L,
        val quizStartDate: MyDate? = null,
        val quizDurationHour: Int = 0,
        val quizDurationMin: Int = 0,
        val correctAnsMarks: Float = 0F,
        val wrongAnsMarks: Float = 0F
) {

    companion object {
        // Binding adapter needs to be static but companion object
        // is not fully static so we have added @JvmStatic annotation
        @BindingAdapter("android:quizImageUrl")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .centerCrop()
                    .into(imageView)
        }

        @BindingAdapter("android:dateFormatter")
        @JvmStatic
        fun formatDateTime(textView: TextView, quizStartTime: MyDate) {
//            Mar 3, 6:22 AM              MMM d, h:mm a

            val simpleDateFormat = SimpleDateFormat("MMM d, h:mm a")
            val cal = Calendar.getInstance()
            cal.set(quizStartTime.year,
                    quizStartTime.month,
                    quizStartTime.date,
                    quizStartTime.quizStartTimeHour,
                    quizStartTime.quizStartTimeMin)

            val formattedDate: String = simpleDateFormat.format(cal.time)
            textView.text = formattedDate
        }

    }

    class MyDate(var year: Int = 0,
                 var month: Int = 0,
                 var date: Int = 0,
                 var quizStartTimeHour: Int = 0,
                 var quizStartTimeMin: Int = 0
    )
}


//class QuizListModel {
//    @DocumentId
//    var quiz_id: String? = null
//
//    // By @DocumentId, firebase will automatically attach document ID to our quiz_id
//    // and that's it to get the document ID.
//    var name: String? = null
//    var description: String? = null
//    var imageUrl: String? = null
//    var level: String? = null
//    var visibility: String? = null
//    var questions = 0
//
//    constructor() {
//        // empty constructor for firebase
//    }
//
//    constructor(name: String?,
//                description: String?,
//                imageUrl: String?,
//                level: String?,
//                visibility: String?,
//                questions: Int) {
//        quiz_id = quiz_id
//        this.name = name
//        this.description = description
//        this.imageUrl = imageUrl
//        this.level = level
//        this.visibility = visibility
//        this.questions = questions
//    }
//}
package com.example.quiz_app_mvvm.model

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import coil.load
import com.example.quiz_app_mvvm.R
import com.google.firebase.firestore.DocumentId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Keep
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
        @BindingAdapter("android:handleImage")
        @JvmStatic
        fun handleImage(imageView: ImageView, imageUrl: String?) {
            if (imageUrl.isNullOrEmpty()) {
                imageView.isGone = true
            } else {
                imageView.isGone = false
                imageView.load(imageUrl)
            }
        }

        @BindingAdapter("android:loadImage")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                imageView.load(imageUrl) {
                    placeholder(R.drawable.placeholder_image)
                }
            }
        }

        @BindingAdapter("android:dateFormatter")
        @JvmStatic
        fun formatDateTime(textView: TextView, quizStartTime: MyDate) {
//            Mar 3, 6:22 AM              MMM d, h:mm a

            val simpleDateFormat = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            val cal = Calendar.getInstance()
            cal.set(
                quizStartTime.year,
                quizStartTime.month,
                quizStartTime.date,
                quizStartTime.quizStartTimeHour,
                quizStartTime.quizStartTimeMin
            )

            val formattedDate: String = simpleDateFormat.format(cal.time)
            textView.text = formattedDate
        }
    }
}

@Keep
class MyDate(
    var year: Int = 0,
    var month: Int = 0,
    var date: Int = 0,
    var quizStartTimeHour: Int = 0,
    var quizStartTimeMin: Int = 0
)

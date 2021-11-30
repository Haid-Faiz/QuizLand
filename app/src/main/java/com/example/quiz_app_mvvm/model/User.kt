package com.example.quiz_app_mvvm.model

import android.widget.ImageView
import androidx.annotation.Keep
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import com.example.quiz_app_mvvm.R
import com.google.firebase.firestore.Exclude

@Keep
data class User(
    val userID: String = "",
    val displayName: String? = "",
    val userImageUrl: String = "",
    @Exclude
    val isNewUser: Boolean = false
) {
    companion object {

        @BindingAdapter("android:quizCircularImageUrl")
        @JvmStatic
        fun loadCircularImage(imageView: ImageView, imageUrl: String?) = imageView.load(imageUrl) {
            placeholder(R.drawable.placeholder_image)
            transformations(CircleCropTransformation())
        }
    }
}

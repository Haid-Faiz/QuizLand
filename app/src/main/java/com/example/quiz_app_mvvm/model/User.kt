package com.example.quiz_app_mvvm.model

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.quiz_app_mvvm.R
import com.google.firebase.firestore.Exclude

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
        public fun loadCircularImage(imageView: ImageView, imageUrl: String?) {
            Glide.with(imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .circleCrop()
                .into(imageView)
        }
    }
}

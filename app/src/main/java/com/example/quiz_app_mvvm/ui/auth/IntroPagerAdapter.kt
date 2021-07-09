package com.example.quiz_app_mvvm.ui.auth

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.IntroPageItemBinding

class IntroPagerAdapter(val context: Context) : RecyclerView.Adapter<IntroPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(
        IntroPageItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ) // returning binding
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 3

    inner class ViewHolder(
        val binding: IntroPageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = binding.apply {
            when (position) {
                0 -> {
                    introPageLottie.setAnimation(R.raw.programmmmmer)
                    introPageLottie.playAnimation()
                    introPageLottie.repeatMode = LottieDrawable.RESTART
                    introPageText.text = context.getString(R.string.intro_page_one_text)
                }
                1 -> {
                    introPageLottie.setAnimation(R.raw.share_anim)
                    introPageLottie.playAnimation()
                    introPageLottie.repeatMode = LottieDrawable.RESTART
                    introPageText.text = context.getString(R.string.intro_page_two)
                }
                2 -> {
                    introPageLottie.setAnimation(R.raw.rocket)
                    introPageLottie.playAnimation()
                    introPageLottie.repeatMode = LottieDrawable.RESTART
                    introPageText.text = context.getString(R.string.intro_page_three)
                }
            }
        }
    }
}

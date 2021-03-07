package com.example.quiz_app_mvvm.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quiz_app_mvvm.views.fragments.IntroFragmentOne
import com.example.quiz_app_mvvm.views.fragments.IntroFragmentThree
import com.example.quiz_app_mvvm.views.fragments.IntroFragmentTwo

class IntroViewPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> IntroFragmentOne()
            1 -> IntroFragmentTwo()
            2 -> IntroFragmentThree()
            else -> IntroFragmentOne()
        }
    }

}
































//class IntroViewPagerAdapter() : PagerAdapter() {
//
//    override fun getCount(): Int {
//        return 3
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//
//        container.addView(LayoutInflater.from(container.context).inflate(R.layout.intro_one, container, false), 0)
//        return when (position) {
//            0 -> LayoutInflater.from(container.context).inflate(R.layout.intro_one, container, false)
//            1 -> LayoutInflater.from(container.context).inflate(R.layout.intro_two, container, false)
//            2 -> LayoutInflater.from(container.context).inflate(R.layout.intro_three, container, false)
//            else -> LayoutInflater.from(container.context).inflate(R.layout.intro_one, container, false)
//        }
//
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
////        super.destroyItem(container, position, `object`)
//    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view == `object`
//    }
//}
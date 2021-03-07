package com.example.quiz_app_mvvm.views.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.quiz_app_mvvm.R
import com.example.quiz_app_mvvm.databinding.ActivityMainBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        setContentView(ActivityMainBinding.inflate(layoutInflater).root)

        // we can only use this line if we added layout tag in its xml
//        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }
}
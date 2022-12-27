package com.adl.project.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivitySelectBinding
import com.adl.project.ui.base.BaseActivity


class SelectActivity :
    BaseActivity<ActivitySelectBinding>(ActivitySelectBinding::inflate, TransitionMode.FADE){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btn1.setOnClickListener {
            startActivity(Intent(applicationContext, MainLineActivity::class.java))
        }

        binding.btn2.setOnClickListener {
            startActivity(Intent(applicationContext, MoveActivity::class.java))
        }
    }

}
package com.adl.project.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityLoginBinding
import com.adl.project.databinding.ActivityMainBinding
import com.adl.project.ui.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate, TransitionMode.FADE) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialize()
    }

    fun setInitialize(){
        binding.btnLogin1.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin2.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

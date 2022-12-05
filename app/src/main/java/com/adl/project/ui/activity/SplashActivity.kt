package com.adl.project.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivitySplashBinding
import com.adl.project.ui.base.BaseActivity

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate, TransitionMode.FADE) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialize()
    }

    private fun setInitialize() {
        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }, 1800)
    }
}
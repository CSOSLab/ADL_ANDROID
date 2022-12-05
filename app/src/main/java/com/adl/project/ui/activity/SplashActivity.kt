package com.adl.project.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivitySplashBinding
import com.adl.project.ui.base.BaseActivity

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 스플래시 화면
 */

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
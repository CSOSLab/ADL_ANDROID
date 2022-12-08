package com.adl.project.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityLoginBinding
import com.adl.project.databinding.ActivitySettingBinding
import com.adl.project.ui.base.BaseActivity

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 설정 화면
 */

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate, TransitionMode.FADE) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialize()
    }

    fun setInitialize(){

    }
}

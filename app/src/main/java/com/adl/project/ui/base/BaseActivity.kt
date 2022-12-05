package com.adl.project.ui.base

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.adl.project.R
import com.adl.project.common.enum.TransitionMode


abstract class BaseActivity<B : ViewBinding>(private val inflate: (LayoutInflater) -> B, private val transitionMode : TransitionMode = TransitionMode.NONE) : AppCompatActivity() {
    protected lateinit var binding: B
        private set
    private var backPressedTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)

        setInitActivityTransition()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.clear()
    }

    override fun onBackPressed() {

        if (isTaskRoot) {
            if (System.currentTimeMillis() > backPressedTime + 2000) {
                backPressedTime = System.currentTimeMillis()
                Toast.makeText(this, "한번 더 누르시면 종료 됩니다", Toast.LENGTH_SHORT).show()
                return
            }
        }

        super.onBackPressed()

        if (isFinishing) {
            when (transitionMode) {
                TransitionMode.HORIZON -> overridePendingTransition(R.anim.horizon_exit_reverse, R.anim.horizon_enter_reverse)
                TransitionMode.VERTICAL -> overridePendingTransition(R.anim.none, R.anim.vertical_exit)
                TransitionMode.FADE -> overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                else -> Unit
            }
        }
    }

    private fun setInitActivityTransition() {
        // 애니메이션 base activity 인자 값에 따라 자동 설정
        when (transitionMode) {
            TransitionMode.HORIZON -> overridePendingTransition(R.anim.horizon_enter, R.anim.horizon_exit)
            TransitionMode.VERTICAL -> overridePendingTransition(R.anim.vertical_enter, R.anim.none)
            TransitionMode.FADE -> overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            else -> Unit
        }
    }
}
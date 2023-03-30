package com.adl.project.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.adl.project.common.Constants
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivitySelectBinding
import com.adl.project.ui.base.BaseActivity


class SelectActivity :
    BaseActivity<ActivitySelectBinding>(ActivitySelectBinding::inflate, TransitionMode.FADE){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btn1.setOnClickListener {
            startActivity(Intent(applicationContext, AdlEventActivity::class.java))
        }

        binding.btn2.setOnClickListener {
            startActivity(Intent(applicationContext, AdlMvActivity::class.java))
        }
        binding.btn3.setOnClickListener {
            val singleChoiceList = arrayOf("온도", "습도", "이산화탄소")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select ENV Mode")

            builder.setItems(singleChoiceList) { _, which ->
                val intent = Intent(applicationContext, AdlEnvActivity::class.java)
                when(which){
                    0 -> intent.putExtra("MODE", Constants.ENVMODE_TEMPERATURE)
                    1 -> intent.putExtra("MODE", Constants.ENVMODE_HUMIDITY)
                    2 -> intent.putExtra("MODE", Constants.ENVMODE_CO2)
                }
                startActivity(intent)
            }

            val alertDialog = builder.create()
            alertDialog.show()

        }
    }

}
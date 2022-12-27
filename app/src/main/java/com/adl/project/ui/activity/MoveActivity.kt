package com.adl.project.ui.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import com.adl.project.R
import com.adl.project.common.*
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityMoveBinding
import com.adl.project.ui.base.BaseActivity
import java.util.*


class MoveActivity :
    BaseActivity<ActivityMoveBinding>(ActivityMoveBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showTableLayout()
    }



    fun showTableLayout() {
        val rowColumn = 5
        binding.tableLayout.setStretchAllColumns(true)
        binding.tableLayout.bringToFront()
        var index = 0
        for (i in 0 until rowColumn) {
            val tr = TableRow(applicationContext)
            for (j in 0 until rowColumn) {
                val tvContent = TextView(applicationContext)
                tvContent.textSize = 25f
                tvContent.gravity = Gravity.CENTER
                tvContent.setText("test" + j)
                tvContent.setBackgroundResource(R.drawable.table_border)
                tvContent.id = 0x01050000 + index
                tvContent.setOnClickListener(this)
                tr.addView(tvContent)
                index += 1
            }
            binding.tableLayout.addView(tr)
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            0x01050000 -> Toast.makeText(applicationContext,"hi ${0x01050000}", Toast.LENGTH_SHORT).show()
            0x01050001 -> Toast.makeText(applicationContext,"hi ${0x01050001}", Toast.LENGTH_SHORT).show()

        }
    }

}
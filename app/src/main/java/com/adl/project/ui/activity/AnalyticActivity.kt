package com.adl.project.ui.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.adl.project.R
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityAnalyticBinding
import com.adl.project.databinding.ActivityLoginBinding
import com.adl.project.ui.base.BaseActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate


class AnalyticActivity :
    BaseActivity<ActivityAnalyticBinding>(ActivityAnalyticBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    var mode = 0
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntents()
        setInitialize()
    }

    fun setInitialize() {
//        binding.tvTest.setText(name + " > " + mode)
        setChart1()
        setChart2()

    }

    fun setChart1(){
        val chart =  binding.piechart1
        chart.setUsePercentValues(true)

        // data Set
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(508f, "수면"))
        entries.add(PieEntry(600f, "요리"))
        entries.add(PieEntry(750f, "활동"))
        entries.add(PieEntry(508f, "씻기"))
        entries.add(PieEntry(670f, "식사"))

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            valueTextColor = Color.BLACK
            colors = ColorTemplate.createColors(ColorTemplate.JOYFUL_COLORS)
            valueTextSize = 16f
        }

        val pieData = PieData(pieDataSet)
        chart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "일상생활\n행동비율"
            setEntryLabelColor(Color.BLACK)
            animateY(1000, Easing.EaseInOutQuad)
            animate()
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        }
    }

    fun setChart2(){
        val chart =  binding.piechart2
        chart.setUsePercentValues(true)

        // data Set
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(508f, "수면"))
        entries.add(PieEntry(600f, "요리"))
        entries.add(PieEntry(750f, "활동"))
        entries.add(PieEntry(508f, "씻기"))
        entries.add(PieEntry(670f, "식사"))

        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.apply {
            valueTextColor = Color.BLACK
            colors = ColorTemplate.createColors(ColorTemplate.PASTEL_COLORS)
            valueTextSize = 16f
        }

        val pieData = PieData(pieDataSet)
        chart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "60대 남성\n평균 행동비율"
            setEntryLabelColor(Color.BLACK)
            animateY(1000, Easing.EaseInOutQuad)
            animate()
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        }
    }

    fun getIntents() {
        if (intent.extras != null) {
            mode = intent.getIntExtra("mode", 1)
            name = intent.getStringExtra("name").toString()
        }
    }

    override fun onClick(view: View?) {
//        when (view?.id) {
//            R.id.btn_1 -> {
//                val intent = Intent(applicationContext, AnalyticActivity::class.java)
//                intent.putExtra("name", "냉장고")
//                intent.putExtra("mode", 1)
//                startActivity(intent)
//            }
//        }
    }
}

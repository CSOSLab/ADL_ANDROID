package com.adl.project.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.adl.project.R
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityMainLineBinding
import com.adl.project.ui.base.BaseActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import java.util.concurrent.TimeUnit

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 메인 실시간 그래프 화면
 */


//https://junyoung-developer.tistory.com/174

class MainLineActivity : BaseActivity<ActivityMainLineBinding>(ActivityMainLineBinding::inflate, TransitionMode.FADE), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialize()
    }


    private fun setInitialize() {

        //TODO -- mpchart, 범례 여러개 사용해서, 각 범례마다 y값 지정 (ex 0,10,20,30,40) 그리고 dot&line graph에서 line 제거해서 표현
        setData(binding.mainChart)
        binding.btnAnal.setOnClickListener(this@MainLineActivity)
        binding.btnSetting.setOnClickListener(this@MainLineActivity)

    }

    private fun setData(chart: LineChart) {
        val entries_0_on = ArrayList<Entry>()
        entries_0_on.add(Entry(-240f,0.0f, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_up_24)))
        entries_0_on.add(Entry(-239f,0.0f,AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_down_24)))
        entries_0_on.add(Entry(5f,0.0f,AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_up_24)))
        entries_0_on.add(Entry(5.5f,0.0f,AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_down_24)))


        val entries2 = ArrayList<Entry>()
        entries2.add(Entry(1.4f,10.0f))
        entries2.add(Entry(3.4f,10.0f))
        entries2.add(Entry(3.6f,10.0f))
        entries2.add(Entry(4.2f,10.0f))

        val entries3 = ArrayList<Entry>()
        entries3.add(Entry(1.4f,20.0f))
        entries3.add(Entry(3.4f,20.0f))
        entries3.add(Entry(3.6f,20.0f))

        val entries4 = ArrayList<Entry>()
        entries4.add(Entry(1.4f,30.0f))
        entries4.add(Entry(3.4f,30.0f))


        chart.run {
            description.isEnabled = true // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
//            setDrawBarShadow(false) //그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
//                axisMaximum = 30f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
//                axisMinimum = 0f // 최소값 0
                granularity = 10f // 50 단위마다 선을 그리려고 설정
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
//                axisLineColor = ContextCompat.getColor(context,R.color.design_default_color_secondary_variant) // 축 색깔 설정
//                gridColor = ContextCompat.getColor(context,R.color.design_default_color_on_secondary) // 축 아닌 격자 색깔 설정
//                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
//                textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) //라벨 색상
                textSize = 12f // 텍스트 크기

                xAxis.valueFormatter = TimeAxisValueFormat()
                xAxis.setDrawLabels(true)  // Label 표시 여부
                xAxis.axisMinimum = -240f  // 오전 5시
                xAxis.axisMaximum = 1200f
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.formSize = 20f
            legend.yEntrySpace = 50f
            legend.xOffset = 20f
            legend.setDrawInside(false)
        }

        var set = LineDataSet(entries_0_on,"환경 경보") // 데이터셋 초기화
        var set2 = LineDataSet(entries2,"전자렌지") // 데이터셋 초기화
        var set3 = LineDataSet(entries3,"변기") // 데이터셋 초기화
        var set4 = LineDataSet(entries4,"냉장고") // 데이터셋 초기화

        set.lineWidth = 0f
        set.setDrawValues(false)
        set2.lineWidth = 0f
        set2.setDrawValues(false)
        set3.lineWidth = 0f
        set3.setDrawValues(false)
        set4.lineWidth = 0f
        set4.setDrawValues(false)

//        set.color = ContextCompat.getColor(applicationContext!!,R.color.main_color)// 바 그래프 색 설정
//        set2.color = ContextCompat.getColor(applicationContext!!,R.color.grey) // 바 그래프 색 설정
//        set3.color = ContextCompat.getColor(applicationContext!!,R.color.purple_200) // 바 그래프 색 설정
//        set4.color = ContextCompat.getColor(applicationContext!!,R.color.teal_200) // 바 그래프 색 설정

        val dataSet :ArrayList<ILineDataSet> = ArrayList()
        dataSet.add(set)
        dataSet.add(set2)
        dataSet.add(set3)
        dataSet.add(set4)

        dataSet.reverse()

        val data = LineData(dataSet)
        chart.run {
            this.data = data //차트의 데이터를 data로 설정해줌.
            invalidate()
            setMaxVisibleValueCount(10000)
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_anal -> {
                val intent = Intent(applicationContext, AnalyticActivity::class.java)
                intent.putExtra("name", "냉장고")
                intent.putExtra("mode", 1)
                startActivity(intent)
            }
            R.id.btn_setting -> {
                val intent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(intent)
            }
        }
    }


}
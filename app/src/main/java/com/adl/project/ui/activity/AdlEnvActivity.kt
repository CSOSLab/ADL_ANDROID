package com.adl.project.ui.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import com.adl.project.R
import com.adl.project.adapter.MainLegendAdapter
import com.adl.project.common.Constants
import com.adl.project.common.enum.TransitionMode
import com.adl.project.common.util.TimeAxisValueFormatManager
import com.adl.project.common.util.UtilManager
import com.adl.project.databinding.ActivityAdlEnvBinding
import com.adl.project.model.adl.*
import com.adl.project.service.HttpService
import com.adl.project.service.SocketIoService
import com.adl.project.ui.base.BaseActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class AdlEnvActivity :
    BaseActivity<ActivityAdlEnvBinding>(ActivityAdlEnvBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    private var MODE = 0
    private var SLIMHUB_NAME : String = ""

    private lateinit var mSocket: Socket
    private var mainLegendAdapter: MainLegendAdapter? = null
    private var selectedStartDate : String = "2023-01-25 00:00:00"
    private var isFirst = true
    private var adlList : AdlListModel? = null
    private var deviceList : DeviceListModel? = null
    private val locationColorMap : MutableMap<String, Int> = mutableMapOf<String, Int>()
    private var labelIndexMap : MutableMap<Float, String>? = mutableMapOf<Float, String>()
    private val locationList : ArrayList<String> = ArrayList()

    val onMessage = Emitter.Listener { args ->
        val obj = args.toString()
        Log.d("DBG:SOCKET.IO::RECEIVED::", obj)
        runOnUiThread {
            if(selectedStartDate.contains(UtilManager.getToday().toString())) {
                Toast.makeText(applicationContext, "실시간 정보 갱신됨!", Toast.LENGTH_SHORT).show()
                setChartWithDate()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //selectedStartDate = UtilManager.getToday().toString() + " 00:00:00" // 앱 시작시에 기준일을 오늘로 변경
        SLIMHUB_NAME = "AB001309" // 슬림허브 네임

        getIntentCode()
        setRealtimeConnection()
        setChartWithDate()

    }

    private suspend fun getDevice(){
        val URL1 = "http://155.230.186.66:8000/devices/"
        val SLIMHUB = SLIMHUB_NAME
        val server1 = HttpService.create(URL1 + SLIMHUB + "/")
        val data = server1.getDeviceData()
        Log.d("DBG:RETRO_DEVICE", data)
        deviceList = Gson().fromJson(data, DeviceListModel::class.java)
    }

    private suspend fun getAdl(startDate: String){
        val SLIMHUB = SLIMHUB_NAME
        var MODENAME = ""
        when(MODE){
            Constants.ENVMODE_TEMPERATURE -> MODENAME = "temperature"
            Constants.ENVMODE_HUMIDITY -> MODENAME = "humidity"
            Constants.ENVMODE_CO2 -> MODENAME = "CO2"
        }
        val URL2 = "http://155.230.186.66:8000/ADLENV/$SLIMHUB/$MODENAME/"
        val server2 = HttpService.create(URL2)
        val endDate = UtilManager.getNextDay(startDate) + " 00:00:00"
        val data = server2.getEnvData(startDate, endDate)
        Log.d("DBG::RETRO_RANGE", startDate + "~" + endDate)
        Log.d("DBG::RETRO_ADL", data)
        adlList = Gson().fromJson(data, AdlListModel::class.java)
    }

    private fun setChartWithDate(){

        // 데이터 초기화
        adlList = null
        deviceList = null
        labelIndexMap?.clear()

        // 메인쓰레드 UI건드리는 작업이므로 코루틴 Dispatchers.Main 사용
        CoroutineScope(Dispatchers.Main).launch {
            connectToServer()
            Log.d("DBG:SETCHART", "CHART")

        }

        // 오늘 날짜일 경우 현재시간 Indicator 1초단위 새로고침
        if(selectedStartDate.contains(UtilManager.getToday().toString())) Timer().scheduleAtFixedRate(1000, 1000) { setAxisWithData() }

    }

    suspend fun connectToServer() {
        // TODO :: 코루틴 도입 -> getDevice, getAdl 을 UI쓰레드에서 분리시키고, 서버연동 과정이 끝나면 차트 그리기. !서버와 연결이 불가능하면 안내문구 띄운 후 앱 종료.
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                getDevice()
                getAdl(selectedStartDate)
            } catch (e:Exception){
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed(java.lang.Runnable { Toast.makeText(applicationContext,"서버와 연결이 불안정해 앱을 종료합니다.", Toast.LENGTH_LONG).show() }, 0)
                finish()
            }
        }

        runBlocking {
            job.join()
            // job이 끝나면, 밑에 코드 실행

            // TODO :: 축 색깔 설정 (첫 실행시에만)
            setAxisColor()
            // TODO :: 축 설정
            setAxisWithData()
            // TODO :: 모두 완료 후에 최종 화면 셋팅
            setInitialize()

            Log.d("DBG:RETRO", deviceList.toString())
            Log.d("DBG:RETRO", adlList.toString())
            Log.d("DBG::RETROSIZE", deviceList!!.data.size.toString() + "::" + adlList!!.data.size.toString())
        }
    }

    private fun setAxisWithData(){
        deviceList?.apply {
//            Log.d("DBG:DATA", data.toString())

            // 최종 라인데이타셋 담을 리스트 선언
            val linedataList : ArrayList<LineDataSet> = ArrayList()
            // 현재시간 표시할 데이터셋 선언 (세로긴줄)
            val entryListNow : ArrayList<Entry> = ArrayList()


            // ADL데이터 차트연동 로직
            for(d in data.indices){
                val deviceType = data[d].location
                val entryList : ArrayList<Entry> = ArrayList()

                adlList?.apply {
                    for(d_ in data){
                        if(deviceType == d_.location){
                            entryList.add(Entry(UtilManager.convertTimeToMin(UtilManager.timestampToTime(d_.time)), d_.value.toFloat()))
                        }
                    }
                }

                // 최종 ADL 데이터셋
                Collections.sort(entryList, EntryXComparator()) // 차트 확대시 NegativeArraySizeException 오류 해결법
                val lineData = LineDataSet(entryList, deviceType)

                // 선택한 이력 날짜가 오늘일 경우 현재시간 실시간 업데이트 (세로긴줄)
                // 오늘 데이터일 경우 현재시간 표시
                val nowHighlightData = LineDataSet(entryListNow, "현재시간")
                nowHighlightData.apply {
                    lineWidth = 1.5f
                    setDrawValues(false)
                    setDrawCircles(false)
                    color = Color.GRAY
                }

                if(selectedStartDate.contains(UtilManager.getToday().toString())){
                    entryListNow.add(Entry(UtilManager.convertTimeToMin(UtilManager.getNow()!!), lineData.yMax))
//                    Log.d("DBG:NOWTIME", UtilManager.convertTimeToMin(UtilManager.getNow()!!).toString())
                }

                // location별 colormap을 실제 라인컬러에 적용한다 (null-safe 처리)
                locationColorMap[data[d].location]?.apply {
                    lineData.color = this
                    lineData.setCircleColor(this)
                    lineData.circleRadius = 6f
                    lineData.circleHoleRadius = 0f
                    lineData.lineWidth = 4f
                }

                linedataList.apply {
                    add(lineData)
                    add(nowHighlightData)
                }
            }

//            Log.d("DBG:LINE", linedataList.toString())
            val dataSet: ArrayList<ILineDataSet> = ArrayList()

            for(ld in linedataList){
                dataSet.add(ld)
            }

            // 모든 과정이 끝나면 차트 그리기
            setData(binding.mainChart, dataSet)
        }
    }

    private fun setData(chart: LineChart, dataSet: ArrayList<ILineDataSet>) {
        val data = LineData(dataSet)
        chart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawGridBackground(false)//격자구조 넣을건지
            setTouchEnabled(true) // 그래프 터치해도 아무 변화없게 막음

            // 앱 실행이 처음일 경우에만 Animation 효과 적용
            if(isFirst){
                animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
                isFirst = false
            }

            axisLeft.run {
                granularity = 10f
                labelCount = data.dataSetCount// 단위마다 선을 그리려고 설정
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정

                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        var label = ""
                        labelIndexMap?.apply {
                            if (containsKey(value)) {
                                label = get(value).toString()
                                // Log.d("DBG::LABEL", label)
                            }
                        }
                        return label
                    }
                }
                textSize = 14f //라벨 텍스트 크기
            }

            xAxis.run {
                // textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) //라벨 색상
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에
                labelCount = 9 // x축 라벨 갯수 (시간 표시 갯수)
                granularity = 0.1f // 1 단위만큼 간격 두기
                setDrawAxisLine(false) // 축 그림
                setDrawGridLines(true) // 격자
                textSize = 15f // 텍스트 크기
                this.valueFormatter = TimeAxisValueFormatManager()
                setDrawLabels(true)  // Label 표시 여부
                axisMinimum = -540f  // -240f : 오전 5시, 0f : 오전 9시, -540f : 00시
                axisMaximum = 900f   // 900f : 00시
            }

            legend.run {
                isEnabled = false //차트 범례 설정
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                orientation = Legend.LegendOrientation.VERTICAL
                formSize = 20f
                yEntrySpace = 50f
                xOffset = 20f
                setDrawInside(false)
            }

            this.data = data //차트의 데이터를 data로 설정해줌.
            invalidate()
            setMaxVisibleValueCount(100000)
            notifyDataSetChanged()
        }
    }


    private fun setAxisColor(){
        if(isFirst) // 처음에만 색깔 랜덤으로 결정해서 locationColorMap에 담고, 이후 갱신시에는 건드리지 않음.
            deviceList?.apply {
                // Location별 Color Map을 만들기 위한 로직
                // DeviceModel의 location 값들을 리스트에 담는다.
                for(d in data.indices){
                    locationList.add(data[d].location)
                }

                for(d in data.indices){
                    locationList.add(data[d].location)
                }

                // locationList 중복 제거 -> Location별 Color Map 만들기 위해서
                locationList.distinct()

                // 각 Location별로 랜덤 컬러를 지정한다.
                for(l in locationList){
                    locationColorMap[l] = Color.rgb(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255))
                }
            }
    }

    private fun setInitialize() {
//        binding.btnAnal.setOnClickListener(this@MainLineActivity)
        binding.btnSetting.setOnClickListener(this@AdlEnvActivity)
        binding.btnDate.setOnClickListener(this@AdlEnvActivity)

        // 날짜 모니터 초기화
        binding.tvDateMonitor.text = selectedStartDate

        // init recyclerview
        mainLegendAdapter = MainLegendAdapter()

        mainLegendAdapter?.setListInit(locationColorMap)

        binding.rvMainLegend.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
            adapter = mainLegendAdapter
        }
    }

    private fun setRealtimeConnection(){
        try{
            mSocket = SocketIoService.get("SOCKET_ADLENV")
            mSocket.on("update_adl", onMessage)
            mSocket.connect()
            Log.d("DBG:SOCKET.IO", "SOCKET.IO CONNECT" + mSocket.id())

            val helloObject = Gson().toJsonTree(AdlSocketModel(SLIMHUB_NAME)).toString()
            Log.d("DBG:JSON", helloObject)
            mSocket.emit("hello", helloObject)
        }catch (e: Exception){
            e.printStackTrace()
            Log.d("DBG:SOCKET.IO", "SOCKET.IO 연결오류")
            Toast.makeText(applicationContext, "실시간대응 소켓 연결실패!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getIntentCode(){
        MODE = getIntent().getIntExtra("MODE",0)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_setting -> {
                val intent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_date -> {
                // 캘린더 온클릭리스너
                // 선택한 날짜를 selectedStartDate로 만든 후 차트 데이터 재연동
                // (month가 0으로 시작하는 issue 있어서 +1 해주기)
                val data = DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    if(month + 1 < 10) {
                        if(day < 10) selectedStartDate = "${year}-0${month + 1}-0${day} 00:00:00"
                        else selectedStartDate = "${year}-0${month + 1}-${day} 00:00:00"
                    }
                    else selectedStartDate = "${year}-${month + 1}-${day} 00:00:00"


                    Log.d("DBG:SELECTEDDATE", selectedStartDate)
                    setChartWithDate()
                }

                // 캘린더객체 생성 (오늘날짜 디폴트선택)
                val cal = Calendar.getInstance()
                DatePickerDialog(this, data, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(
                    Calendar.DAY_OF_MONTH)).show()
            }
        }
    }
}
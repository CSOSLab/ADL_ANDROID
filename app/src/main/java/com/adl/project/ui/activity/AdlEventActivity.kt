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
import com.adl.project.common.enum.TransitionMode
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.common.util.TimeAxisValueFormatManager
import com.adl.project.common.util.UtilManager
import com.adl.project.databinding.ActivityAdlEnvBinding
import com.adl.project.databinding.ActivityAdlEventBinding
import com.adl.project.databinding.ActivityAdlMainBinding
import com.adl.project.model.adl.AdlListModel
import com.adl.project.model.adl.AdlSocketModel
import com.adl.project.model.adl.DeviceListModel
import com.adl.project.model.adl.DeviceModel
import com.adl.project.service.HttpService
import com.adl.project.service.SocketIoService
import com.adl.project.ui.base.BaseActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: ADL_EVENT 실시간 그래프 화면
 */

class AdlEventActivity :
    BaseActivity<ActivityAdlEventBinding>(ActivityAdlEventBinding::inflate, TransitionMode.FADE),
    View.OnClickListener, AdapterClickListener {

    private var SLIMHUB_NAME : String = ""

    private lateinit var mSocket: Socket
    private var mainLegendAdapter: MainLegendAdapter? = null
    private var selectedStartDate : String = "2023-04-01 00:00:00"
    private var isFirst = true
    private var adlList : AdlListModel? = null
    private var deviceList : DeviceListModel? = null
    private val locationColorMap : MutableMap<String, Int> = mutableMapOf<String, Int>()
    private val locationIndexMap : MutableMap<String, Float> = mutableMapOf<String, Float>()

    private var labelIndexMap : MutableMap<Float, String>? = mutableMapOf<Float, String>()
    private var locationList : ArrayList<String> = ArrayList()

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
//        selectedStartDate = UtilManager.getToday().toString() + " 00:00:00" // 앱 시작시에 기준일을 오늘로 변경
        SLIMHUB_NAME = "AB001309" // 슬림허브 네임

        setRealtimeConnection()
        setChartWithDate()
    }

    private fun setInitialize() {
//        binding.btnAnal.setOnClickListener(this@MainLineActivity)
        binding.btnSetting.setOnClickListener(this@AdlEventActivity)
        binding.btnDate.setOnClickListener(this@AdlEventActivity)

        // 날짜 모니터 초기화
        binding.tvDateMonitor.text = selectedStartDate

        // init recyclerview
        mainLegendAdapter = MainLegendAdapter()

        mainLegendAdapter?.let {
            it.setItemClickListener(this@AdlEventActivity)
            it.setListInit(locationColorMap)
        }

        binding.rvMainLegend.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
            adapter = mainLegendAdapter
        }

    }

    private fun setRealtimeConnection(){
        try{
            mSocket = SocketIoService.get("ADL_NOTIFIER")
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

    /* TODO :: 서버 연결 ->
     *  1. 디바이스 정보 받아와서 축 요소 세팅
     *  2. ADL 정보 받아와서 차트 그리기 */

    private suspend fun getDevice(){
        val URL1 = "http://155.230.186.66:8000/devices/"
        val SLIMHUB = SLIMHUB_NAME
        val server1 = HttpService.create(URL1 + SLIMHUB + "/")
        val data = server1.getDeviceData()
        Log.d("DBG:RETRO_DEVICE", data)
        deviceList = Gson().fromJson(data, DeviceListModel::class.java)
    }

    private suspend fun getAdl(startDate: String){
        val URL2 = "http://155.230.186.66:8000/ADL_EVENTs/"
        val SLIMHUB = SLIMHUB_NAME
        val server2 = HttpService.create(URL2 + SLIMHUB + "/")

        val endDate = UtilManager.getNextDay(startDate) + " 00:00:00"
        val data = server2.getEventData(startDate, endDate)
        Log.d("DBG::RETRO_RANGE", startDate + "~" + endDate)
        Log.d("DBG::RETRO_ADL", data)
        adlList = Gson().fromJson(data, AdlListModel::class.java)
    }

    suspend fun connectToServer() {
        // TODO :: 코루틴 도입 -> getDevice, getAdl 을 UI쓰레드에서 분리시키고, 서버연동 과정이 끝나면 차트 그리기. !서버와 연결이 불가능하면 안내문구 띄운 후 앱 종료.
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                getDevice()
                getAdl(selectedStartDate)
            } catch (e:Exception){
                e.printStackTrace()
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
//            Log.d("DBG::RETROSIZE", deviceList!!.data.size.toString() + "::" + adlList!!.data.size.toString())
        }

    }

    private fun setAxisColor(){
        val custom2 = DeviceModel("1234", "이상상황", "이상상황", 1)
        deviceList?.data?.add(custom2)

        if(isFirst) // 처음에만 색깔 랜덤으로 결정해서 locationColorMap에 담고, 이후 갱신시에는 건드리지 않음.
            deviceList?.apply {
                // Location별 Color Map을 만들기 위한 로직
                // DeviceModel의 location 값들을 리스트에 담는다.
                for(d in data.indices){
                    locationList.add(data[d].location)
                }

                // locationList 중복 제거 -> Location별 Color Map 만들기 위해서
                locationList = locationList.distinct() as ArrayList<String>

                var lindex = 0
                for(l in locationList){
                    locationIndexMap[l] = lindex * 10f

                    // 이상상황일 경우 무조건 RED 컬러 배치
                    if(l == "이상상황") {
                        locationColorMap[l] = Color.RED
                    }
                    else{
                        locationColorMap[l] = Color.rgb(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255))
                    }

                    lindex += 1
                }
            }
    }

    private fun setAxisWithData(){


        locationList.apply {
//            Log.d("DBG:DATA", data.toString())

            // 최종 라인데이타셋 담을 리스트 선언
            val linedataList : ArrayList<LineDataSet> = ArrayList()
            // 현재시간 표시할 데이터셋 선언 (세로긴줄)
            val entryListNow : ArrayList<Entry> = ArrayList()

            Log.d("DBG:LABELINDEX", this.toString())

            // ADL데이터 차트연동 로직
            for(d in this){
                val entryList : ArrayList<Entry> = ArrayList()

                // 라벨인덱스를 map 자료형에 미리 저장한다.
                labelIndexMap?.put(locationIndexMap[d]!!, d)

                // 각 type별 y축을 쭉 그리기 위해, x축 0과 1440 위치에 투명한 circle을 그린다.
                entryList.add(Entry(-540f, locationIndexMap[d]!!, AppCompatResources.getDrawable(applicationContext, android.R.color.transparent)))
                entryList.add(Entry(899f, locationIndexMap[d]!!, AppCompatResources.getDrawable(applicationContext, android.R.color.transparent)))

                // 각 라벨리스트를 순회하며 Adl 수신 값중에 해당하는 type이 있는지 찾는다.
                adlList?.apply {
                    for(d_ in data){

                        //test
                        if("start" == d_.type){
                            if(d == d_.location){
                                entryList.add(Entry(UtilManager.convertTimeToMin(UtilManager.timestampToTime(d_.time)), locationIndexMap[d_.location]!!, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_up_24)))
                            }
                        }
                        if("end" == d_.type){
                            if(d == d_.location){
                                entryList.add(Entry(UtilManager.convertTimeToMin(UtilManager.timestampToTime(d_.time)), locationIndexMap[d_.location]!!, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_down_24)))
                            }
                        }
                    }
                }

                // 최종 ADL 데이터셋
                Collections.sort(entryList, EntryXComparator()) // 차트 확대시 NegativeArraySizeException 오류 해결법
                val lineData = LineDataSet(entryList, d)

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
                lineData.color = locationColorMap[d]!!


                lineData.apply {
                    lineWidth = 4f
                    setDrawValues(false)
                    setDrawCircles(false)
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
            Log.d("DBG::dataset", dataSet.toString())

            // 모든 과정이 끝나면 차트 그리기
            setData(binding.mainChart, dataSet)
        }
    }

    private fun setData(chart: LineChart, dataSet: ArrayList<ILineDataSet>) {
        // 테스트 더미 데이터 코드
        /* val entries_0_on = ArrayList<Entry>()
        entries_0_on.add(Entry(convertTimeToMin("10:44:23"), 0.0f, AppCompatResources.getDrawable(applicationContext, R.drawable.ic_baseline_arrow_drop_up_24)))
        var set = LineDataSet(entries_0_on, "환경 경보") // 데이터셋 초기화
        set.lineWidth = 0f
        set.setDrawValues(false)
        val dataSet: ArrayList<ILineDataSet> = ArrayList()
        dataSet.reverse() */

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

            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.

            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                // setDrawBarShadow(false) //그래프의 그림자
                // axisLineColor = ContextCompat.getColor(context,R.color.design_default_color_secondary_variant) // 축 색깔 설정
                // gridColor = ContextCompat.getColor(context,R.color.design_default_color_on_secondary) // 축 아닌 격자 색깔 설정
                // textColor = ContextCompat.getColor(context,R.color.design_default_color_primary_dark) // 라벨 텍스트 컬러 설정
                // axisMaximum = 70f //100 위치에 선을 그리기 위해 101f로 맥시멈값 설정
                // axisMinimum = 0f // 최소값 0
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

    override fun onClick(view: View?) {
        when (view?.id) {
//            R.id.btn_anal -> {
//                val intent = Intent(applicationContext, AnalyticActivity::class.java)
//                intent.putExtra("name", "냉장고")
//                intent.putExtra("mode", 1)
//                startActivity(intent)
//            }
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
                DatePickerDialog(this, data, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    override fun onItemClick(clickData: Any?, clickFrom: String?) {
        // Recyclerview Click Listener 구현자리
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try{ mSocket.disconnect() }catch (_:Exception){}
    }

}
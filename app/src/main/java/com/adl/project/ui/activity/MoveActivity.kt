package com.adl.project.ui.activity

import android.app.ActionBar.LayoutParams
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adl.project.R
import com.adl.project.adapter.MainLegendAdapter
import com.adl.project.common.*
import com.adl.project.common.enum.TransitionMode
import com.adl.project.common.util.UtilManager
import com.adl.project.databinding.ActivityMoveBinding
import com.adl.project.model.adl.AdlSocketModel
import com.adl.project.model.adl.DeviceListModel
import com.adl.project.model.adlmvhistory.AdlMvModel
import com.adl.project.model.adlmvhistory.MvHistoryListModel
import com.adl.project.service.HttpService
import com.adl.project.service.SocketIoService
import com.adl.project.ui.base.BaseActivity
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*


class MoveActivity :
    BaseActivity<ActivityMoveBinding>(ActivityMoveBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    private var SLIMHUB_NAME : String = ""
    private lateinit var mSocket: Socket

    private var mainLegendAdapter: MainLegendAdapter? = null
    private var curLocation : String? = null
    private var adlmvHistoryList : List<MvHistoryListModel>? = null
    private var deviceList : DeviceListModel? = null
    private var selectedStartDate : String = "2022-12-23"
    private val locationColorMap : MutableMap<String, Int> = mutableMapOf<String, Int>()
    private var locationList : List<String> = listOf()
    private var rows : MutableList<TableRow> = mutableListOf()
    private var clickListenerList : MutableList<Int> = mutableListOf()
    private var clickListenerDataList : MutableList<String> = mutableListOf()

    val onMessage = Emitter.Listener { args ->
        val obj = args.toString()
        Log.d("DBG:SOCKET.IO::RECEIVED::", obj)
        runOnUiThread {
            Toast.makeText(applicationContext, "실시간 정보 갱신됨!", Toast.LENGTH_SHORT).show()
            if(UtilManager.getToday().toString() == selectedStartDate) getServerData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedStartDate = UtilManager.getToday().toString() // 앱 시작시에 기준일을 오늘로 변경
        SLIMHUB_NAME = "AB001309" // 슬림허브 네임

        setRealtimeConnection()
        getServerData()
    }

    private fun setRealtimeConnection(){
        try{
            mSocket = SocketIoService.get()
            mSocket.on("update_adlmv", onMessage)
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

    private suspend fun getDevice(){
        val URL1 = "http://155.230.186.66:8000/devices/"
        val SLIMHUB = "AB001309"
        val server1 = HttpService.create(URL1 + SLIMHUB + "/")
        val data = server1.getDeviceData()
        Log.d("DBG:RETRO_DEVICE", data)
        deviceList = Gson().fromJson(data, DeviceListModel::class.java)
    }

    private suspend fun getAdlmvHistory(startDate: String){
        val URL2 = "http://155.230.186.66:8000/ADLMVHistory/"
        val SLIMHUB = "AB001309"
        val server2 = HttpService.create(URL2 + SLIMHUB + "/")

        val endDate = UtilManager.getNextDay(startDate)
        val data = server2.getHistoryData(startDate, endDate)
        Log.d("DBG::RETRO_HISTORY", data)

        val adlmvList = Gson().fromJson(data, AdlMvModel::class.java)
        adlmvList?.let {
            adlmvHistoryList = it.data.mvHistory
            curLocation = it.data.curLocation
            Log.d("DBG::MVHISTORY", adlmvHistoryList.toString())
            Log.d("DBG::CURLOC", curLocation.toString())
        }
    }

    fun getServerData(){
        // 데이터 초기화
        adlmvHistoryList = null
        deviceList = null

        // 메인쓰레드 UI건드리는 작업이므로 코루틴 Dispatchers.Main 사용
        CoroutineScope(Dispatchers.Main).launch {
            connectToServer()
        }
    }

    suspend fun connectToServer() {
        // TODO :: 코루틴 도입 -> getDevice, getAdl 을 UI쓰레드에서 분리시키고, 서버연동 과정이 끝나면 차트 그리기. !서버와 연결이 불가능하면 안내문구 띄운 후 앱 종료.
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                getDevice()
                getAdlmvHistory(selectedStartDate)
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
            setDeviceLegend()
            setInitialize()
            showTableLayout()

        }

    }

    fun showTableLayout() {
        val rowColumn = locationList.size + 1
        binding.tableLayout.setStretchAllColumns(true)
        binding.tableLayout.bringToFront()

        // 기존 동적 생성했던 테이블 뷰 모두 삭제하고 시작
        // 이 과정 안하면 달력으로 새로운 일자 선택시 테이블이 밑에 쌓이면서 추가됨
        try {
            for (r in rows) {
                binding.tableLayout.removeView(r)
                Log.d("DBG::TABLE",rows.toString())
            }
        }
        catch (_ : Exception){}

        var index = 0x0
        for (i in 0 until rowColumn) {
            val tr = TableRow(applicationContext)
            for (j in 0 until rowColumn) {
                val tvContent = TextView(applicationContext)
                var isClickNeed = false
                // 각 축에 범례 세팅
                try{
                    if(i == 0 && j > 0) tvContent.setText(locationList[j-1])
                    else if(j == 0 && i > 0) tvContent.setText(locationList[i-1])
                    else if(j == 0 && i == 0) tvContent.setText("↔️")

                    // curlocation 값을 받아서 현재 위치에 색깔 강조 박스 표시
                    else if(i == j){
                        if(locationList[i-1] == curLocation) {
                            tvContent.setBackgroundResource(R.drawable.table_border_curlocation)
                        }else{
                            tvContent.setBackgroundResource(R.drawable.table_border)
                        }
                    }

                    // 각 로케이션별 해당값 찾아서 넣기
                    else {
                        //adlmvHistoryList가 비어있지 않으면, 순회하면서 from, to에 해당하는 값에 값 찾아 넣기
                        adlmvHistoryList?.let{
                            for(mvHis in it){
                                if(locationList[j-1] == mvHis.from){
                                    for(g in mvHis.goals){
                                        if(locationList[i-1] == g.to){
                                            //Log.d("DBG::SETTABLE", locationList[i-1] + g.to)
                                            tvContent.setText("${g.move_freq}회 (${g.avg_time}초)")
                                            isClickNeed = true
                                        }
                                    }
                                }
                            }
                        }
                        tvContent.setBackgroundResource(R.drawable.table_border)
                    }
                }catch (_: IndexOutOfBoundsException){ }

                tvContent.apply {
                    textSize = 25f
                    gravity = Gravity.CENTER
                    setTextColor(Color.BLACK)
                    id = 0x01050000 + index
                    if(isClickNeed) {
                        clickListenerList.add(id)
                        clickListenerDataList.add(locationList[j-1] + "_" + locationList[i-1])
                        // Log.d("DBG::CLICKDATA", id.toString() + "_" + locationList[j-1] + "_" + locationList[i-1])
                    }
                    setOnClickListener(this@MoveActivity)
                    tr.addView(this)
                }

                index += 0x1
            }
            rows.add(tr)
            binding.tableLayout.addView(tr)
        }

    }

    fun setInitialize(){
        binding.btnSetting.setOnClickListener(this@MoveActivity)
        binding.btnDate.setOnClickListener(this@MoveActivity)

        // 날짜 모니터 초기화
        binding.tvDateMonitor.text = selectedStartDate

        // init recyclerview
        mainLegendAdapter = MainLegendAdapter()

        mainLegendAdapter?.let {
            it.setListInit(locationColorMap)
        }

        binding.rvMainLegend.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, true)
            adapter = mainLegendAdapter
        }
    }

    fun setDeviceLegend(){

        deviceList?.let {
            // Location별 Color Map을 만들기 위한 로직
            // DeviceModel의 location 값들을 리스트에 담는다.
            val locationList_ : MutableList<String> = mutableListOf()

            for(d in it.data.indices){
                locationList_.add(it.data[d].location)
            }

            // locationList 중복 제거 -> Location별 Color Map 만들기 위해서
            locationList = locationList_.distinct()

            // 각 Location별로 랜덤 컬러를 지정한다.
            for(l in locationList){
                locationColorMap[l] = Color.rgb(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255))
            }

        }

    }

    override fun onClick(p0: View?) {
        Log.d("DBG::ID",(p0?.id).toString())

        // 미리 클릭리스너가 필요한 칸을 등록해놓고, 해당 칸을 클릭하면 디테일보기 창으로 넘어감
        var index = 0
        for(c in clickListenerList){
            if(p0?.id == c){
                val intent = Intent(applicationContext, MoveDetailActivity::class.java)
                intent.putExtra("date", selectedStartDate)
                intent.putExtra("from", clickListenerDataList[index].split("_")[0])
                intent.putExtra("to", clickListenerDataList[index].split("_")[1])
                startActivity(intent)
            }
            index += 1
        }

        when(p0?.id){
            R.id.btn_setting -> {
                val intent = Intent(applicationContext, SettingActivity::class.java)
                startActivity(intent)
            }
            R.id.btn_date -> {
                // 캘린더 온클릭리스너
                // 선택한 날짜를 selectedStartDate로 만든 후 차트 데이터 재연동
                // (month가 0으로 시작하는 issue 있어서 +1 해주기)
                val data = DatePickerDialog.OnDateSetListener { view, year, month, day ->
                    selectedStartDate = "${year}-${month + 1}-${day}"
                    Log.d("DBG::SELECTEDDATE", selectedStartDate)
                    getServerData()
                }

                // 캘린더객체 생성 (오늘날짜 디폴트선택)
                val cal = Calendar.getInstance()
                DatePickerDialog(this, data, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try{ mSocket.disconnect() }catch (_:Exception){}
    }

}
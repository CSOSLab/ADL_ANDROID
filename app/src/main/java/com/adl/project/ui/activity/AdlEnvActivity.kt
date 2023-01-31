package com.adl.project.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adl.project.adapter.MainLegendAdapter
import com.adl.project.common.Constants
import com.adl.project.common.enum.TransitionMode
import com.adl.project.common.util.UtilManager
import com.adl.project.databinding.ActivityAdlEnvBinding
import com.adl.project.model.adl.AdlEnvListModel
import com.adl.project.model.adl.AdlListModel
import com.adl.project.model.adl.AdlSocketModel
import com.adl.project.model.adl.DeviceListModel
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
import java.util.ArrayList

class AdlEnvActivity :
    BaseActivity<ActivityAdlEnvBinding>(ActivityAdlEnvBinding::inflate, TransitionMode.FADE),
    View.OnClickListener {

    private var MODE = 0
    private var SLIMHUB_NAME : String = ""

    private lateinit var mSocket: Socket
    private var mainLegendAdapter: MainLegendAdapter? = null
    private var selectedStartDate : String = "2023-01-25T00:00:00"
    private var isFirst = true
    private var adlList : AdlEnvListModel? = null
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
                //setChartWithDate()
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
        getServerData()

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
        val endDate = UtilManager.getNextDay(startDate) + "T00:00:00"
        val data = server2.getEnvData(startDate, endDate)
        Log.d("DBG::RETRO_RANGE", startDate + "~" + endDate)
        Log.d("DBG::RETRO_ADL", data)
        adlList = Gson().fromJson(data, AdlEnvListModel::class.java)
    }

    fun getServerData(){
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
            //setAxisColor()
            // TODO :: 축 설정
            //setAxisWithData()
            // TODO :: 모두 완료 후에 최종 화면 셋팅
            setInitialize()

            Log.d("DBG:RETRO", deviceList.toString())
            Log.d("DBG:RETRO", adlList.toString())
            Log.d("DBG::RETROSIZE", deviceList!!.data.size.toString() + "::" + adlList!!.data.size.toString())
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
    }
}
package com.adl.project.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adl.project.R
import com.adl.project.adapter.AdlMvsDetailAdapter
import com.adl.project.adapter.MainLegendAdapter
import com.adl.project.common.enum.TransitionMode
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.common.util.UtilManager
import com.adl.project.databinding.ActivityMoveBinding
import com.adl.project.databinding.ActivityMoveDetailBinding
import com.adl.project.model.adlmvhistory.AdlMvModel
import com.adl.project.model.adlmvhistory.MvHistoryListModel
import com.adl.project.model.adlmvs.AdlMvsListModel
import com.adl.project.model.adlmvs.AdlMvsModel
import com.adl.project.service.HttpService
import com.adl.project.ui.base.BaseActivity
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MoveDetailActivity :
    BaseActivity<ActivityMoveDetailBinding>(ActivityMoveDetailBinding::inflate, TransitionMode.FADE),
    View.OnClickListener , AdapterClickListener {

    private var adlmvsAdapter: AdlMvsDetailAdapter? = null
    private var adlmvsList : List<AdlMvsModel>? = null
    private var selectedStartDate : String = "2022-12-23"
    private var fromLocation : String = ""
    private var toLocation : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_move_detail)

        getIntents()
        getServerData()
    }

    fun getIntents(){
        val intent = getIntent()
        fromLocation = intent.getStringExtra("from").toString()
        toLocation = intent.getStringExtra("to").toString()
        selectedStartDate = intent.getStringExtra("date").toString()
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
                getAdlMvs(selectedStartDate)
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
            setData()
        }

    }

    private suspend fun getAdlMvs(startDate: String){
        val URL2 = "http://155.230.186.66:8000/ADLMVs/"
        val SLIMHUB = "AB001309"
        val server2 = HttpService.create(URL2 + SLIMHUB + "/")

        val endDate = UtilManager.getNextDay(startDate)
        val data = server2.getMvsData(startDate, endDate, fromLocation, toLocation)
        Log.d("DBG::RETRO_MVS", data)

        val adlmvs = Gson().fromJson(data, AdlMvsListModel::class.java)
        adlmvs?.let {
            adlmvsList = it.data
            Log.d("DBG::MVS", adlmvsList.toString())
        }
    }

    private fun setData(){
        adlmvsAdapter = AdlMvsDetailAdapter()
        adlmvsAdapter?.let {
            it.setListInit(adlmvsList!!)
            it.setItemClickListener(this@MoveDetailActivity)

        }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
            adapter = adlmvsAdapter
        }
        adlmvsAdapter?.notifyDataSetChanged()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(p0: View?) {
        //TODO("Not yet implemented")
    }

    override fun onItemClick(clickData: Any?, clickFrom: String?) {
        TODO("Not yet implemented")
    }
}
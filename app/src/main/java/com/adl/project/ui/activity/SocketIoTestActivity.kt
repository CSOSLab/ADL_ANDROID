package com.adl.project.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.adl.project.R
import com.adl.project.model.adl.AdlSocketModel
import com.adl.project.service.SocketIoService
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter

class SocketIoTestActivity : AppCompatActivity() {
    private lateinit var mSocket: Socket
    val onMessage = Emitter.Listener { args ->
        val obj = args.toString()
        Log.d("DBG:SOCKET.IO::RECEIVED::", obj.toString())
        runOnUiThread {
            Toast.makeText(applicationContext, obj.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        setRealtimeConnection()

    }
    private fun setRealtimeConnection(){
        try{
            mSocket = SocketIoService.get("ADL_NOTIFIER")
            mSocket.on("update_adl", onMessage)
            mSocket.connect()

            val helloObject = Gson().toJsonTree(AdlSocketModel("AB001309")).toString()
            Log.d("DBG:JSON", helloObject)
            Log.d("DBG:SOCKET.IO", "SOCKET.IO CONNECT" + mSocket.id())
            mSocket.emit("hello", helloObject)
        }catch (e: Exception){
            e.printStackTrace()
            Log.d("DBG:SOCKET.IO", "SOCKET.IO 연결오류")
            Toast.makeText(applicationContext, "실시간대응 소켓 연결실패!", Toast.LENGTH_SHORT).show()
        }
    }
}
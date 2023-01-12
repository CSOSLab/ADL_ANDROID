package com.adl.project.service

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.Transport
import io.socket.engineio.client.transports.WebSocket
import java.net.SocketOptions
import java.net.URI
import java.net.URISyntaxException

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: SOCKET.IO 통신을 위한 매니저클래스
 */

class SocketIoService {
    companion object {
        private lateinit var socket : Socket
        fun get(): Socket {
            try {
                val opts = IO.Options()
                opts.path = "/ADL_DATA_UPDATE/"
                opts.transports = arrayOf(WebSocket.NAME)
                socket = IO.socket("http://155.230.186.66:8000",opts)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("DBG::SOCKET.IO",e.toString())
            }
            return socket
        }
    }
}


//    https://velog.io/@tera_geniel/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9Ckotlin%EC%99%80-nodejs-socket.io%EB%A1%9C-%ED%86%B5%EC%8B%A0%ED%95%98%EA%B8%B0

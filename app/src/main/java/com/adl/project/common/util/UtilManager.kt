package com.adl.project.common.util

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 프로젝트에 필요한 각종 유틸 메소드를 전역으로 정의하는 UtilManager 클래스
 */

class UtilManager {
    // 전역 메소드를 만들기 위한 companion object.
    // Util 클래스를 따로 만들어 불필요한 보일러플레이트 코드를 생략
    // 자바의 static과 비슷한 역할 (내부구현상 똑같지는 않음)
    companion object {
        fun convertTimeToMin(value: String): Float {
            // HH:mm:ss
            var timeMin = 0.0
            try {
                timeMin += value.split(":")[1].toInt() //분
                timeMin += value.split(":")[0].toInt() * 60 //시
                timeMin += value.split(":")[2].toInt() / 60.0

            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 오전 9시가 0이 되어야하는 상황, 오전9시 기준이기 때문에 540빼줌
            timeMin -= 540f
            // 오전 7시 처리
            if (timeMin < 0) {
                timeMin = 1440f + timeMin
            }
            // Log.d("time", timeMin.toString())

            return timeMin.toFloat()
        }

        // 서버에서 받은 Timestamp 변환 위해 Locale.KOREA 필요 (리스폰스 값은 UTC기준으로 들어옴)
        fun timestampToTime(timestamp: Timestamp): String {
            val time = timestamp.time
            // hh -> HH 변경....;;;;; HH -> 24 기준시
            val res = SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(Date(time))
            // Log.d("DBG::TIME", res.toString())
            return res
        }

        fun getNextDay(startDate: String): String {
            var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            var calendar = Calendar.getInstance()
            calendar.time = sdf.parse(startDate)!!
            calendar.add(Calendar.DATE, 1)

            // Log.d("DBG::TIME", sdf.format(calendar.time))
            return sdf.format(calendar.time)
        }

        // 오늘 날짜 구하기
        fun getToday(): String? {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            return dateFormat.format(Date(System.currentTimeMillis()))
        }

        // 현재 시간 구하기
        fun getNow(): String? {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
            return dateFormat.format(Date(System.currentTimeMillis()))
        }
    }
}
package com.adl.project.common.util

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeAxisValueFormat : IndexAxisValueFormatter() {

    //https://junyoung-developer.tistory.com/174

    override fun getFormattedValue(value: Float): String {

        // Float(min) -> Date
        var valueToMinutes = TimeUnit.MINUTES.toMillis(value.toLong())
        var timeMimutes = Date(valueToMinutes)
        var formatMinutes = SimpleDateFormat("HH:mm")

        return formatMinutes.format(timeMimutes)
    }
}
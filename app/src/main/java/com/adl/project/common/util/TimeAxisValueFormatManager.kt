package com.adl.project.common.util

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TimeAxisValueFormatManager : IndexAxisValueFormatter() {

    //https://junyoung-developer.tistory.com/174

    override fun getFormattedValue(value: Float): String {

        // Float(min) -> Date
        var valueToMinutes = TimeUnit.MINUTES.toMillis(value.toLong())
        var timeMimutes = Date(valueToMinutes)
        var formatMinutes = SimpleDateFormat("HH:mm")

        return formatMinutes.format(timeMimutes)
    }
}
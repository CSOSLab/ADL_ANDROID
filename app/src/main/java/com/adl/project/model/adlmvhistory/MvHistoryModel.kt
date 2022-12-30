package com.adl.project.model.adlmvhistory

import java.sql.Timestamp

data class MvHistoryModel(
    val to: String, val avg_time: Int, val move_freq: Int
)

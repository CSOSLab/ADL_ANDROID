package com.adl.project.model.adlevent

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class AdlEventModel(
    val id: Int, val location: String, val start_time: Timestamp, val end_time: Timestamp, val adl_event: String, val state: String?
)

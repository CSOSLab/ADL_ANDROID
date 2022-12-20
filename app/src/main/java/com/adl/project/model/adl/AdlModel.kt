package com.adl.project.model.adl

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp
import java.util.*

data class AdlModel(
    val location: String, val time: Timestamp, val type: String, val value: String
)

package com.adl.project.model.adl

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class AdlModel(
    val id: Int, val location: String, val time: Timestamp, val type: String, val value: String
)

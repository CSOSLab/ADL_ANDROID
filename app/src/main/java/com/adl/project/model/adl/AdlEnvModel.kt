package com.adl.project.model.adl

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class AdlEnvModel(
    val id: Int, val sh_id: String, val location: String, val time: Timestamp, val type: String, val value: String,
)

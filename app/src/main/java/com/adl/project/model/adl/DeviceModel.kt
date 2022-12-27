package com.adl.project.model.adl

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp
import java.util.*

//{"data":[{"sh_id":"AB001309","location":"거실","type":"TV","no":1},
data class DeviceModel(
    val sh_id: String, val location: String, val type: String, val no: Int
)

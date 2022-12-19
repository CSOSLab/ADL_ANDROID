package com.adl.project.model.adl

import com.google.gson.annotations.SerializedName

data class AdlModel(
    @SerializedName("location") var location: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("value") var value: String? = null
)

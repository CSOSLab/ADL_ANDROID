package com.adl.project.model.adl

import com.adl.project.model.adl.AdlModel
import com.google.gson.annotations.SerializedName

data class MainResponseModel(
    @SerializedName("_data_") var _data_: List<AdlModel>
    )

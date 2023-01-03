package com.adl.project.model.adlmvs

import com.adl.project.model.adl.AdlModel
import java.sql.Timestamp

data class AdlMvsModel(
    val fromTime: String, val toTime: String, val timeDiff: String, val fromLocation: String, val toLocation: String
)

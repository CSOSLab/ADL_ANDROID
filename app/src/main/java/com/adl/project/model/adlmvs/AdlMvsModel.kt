package com.adl.project.model.adlmvs

import com.adl.project.model.adl.AdlModel
import java.sql.Timestamp

data class AdlMvsModel(
    val fromTime: Timestamp, val toTime: Timestamp, val timeDiff: String, val fromLocation: String, val toLocation: String
)

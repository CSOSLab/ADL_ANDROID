package com.adl.project.model.adlmvhistory

import java.sql.Timestamp

data class MvHistoryListModel(
    val from : String, val goals : List<MvHistoryModel>
)

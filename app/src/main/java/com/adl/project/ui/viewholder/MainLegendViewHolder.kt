package com.adl.project.ui.viewholder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.databinding.ListItemMainLegendBinding
import com.adl.project.databinding.ListItemSampleMainBinding
import com.adl.project.model.test.SampleBoardInfo
import com.adl.project.ui.viewholder.MainSampleViewHolder

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 메인 화면의 Location Legend 표시를 위한 리사이클러뷰 뷰홀더
 */

class MainLegendViewHolder(private var binding: ListItemMainLegendBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(name: String, color: Int, _listener: AdapterClickListener?) {
        binding.sqColor.setBackgroundColor(color)
        binding.tvContent.text = name
    }
}
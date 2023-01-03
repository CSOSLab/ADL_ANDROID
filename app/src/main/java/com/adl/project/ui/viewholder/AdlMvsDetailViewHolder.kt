package com.adl.project.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.databinding.ListItemAdlmvsDetailBinding

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: AdlMvs 디테일 화면의 데이터표시를 위한 리사이클러뷰 어댑터
 */

class AdlMvsDetailViewHolder(private var binding: ListItemAdlmvsDetailBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(fromtime: String, totime : String,  difftime: String, _listener: AdapterClickListener?) {
        binding.tvTime.text = fromtime + " ~ " + totime
        binding.tvDifftime.text = difftime
    }
}
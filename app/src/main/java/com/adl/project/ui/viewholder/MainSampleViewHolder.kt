package com.adl.project.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.databinding.ListItemSampleMainBinding
import com.adl.project.model.test.SampleBoardInfo


class MainSampleViewHolder(private var binding: ListItemSampleMainBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: SampleBoardInfo, _listener: AdapterClickListener?) {
        binding.tvTitle.text = data.title
        binding.tvContent.text = data.content
        binding.root.setOnClickListener {
            _listener?.onItemClick(data, "sample")
        }
    }
}
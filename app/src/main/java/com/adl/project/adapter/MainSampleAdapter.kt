package com.adl.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.databinding.ListItemSampleMainBinding
import com.adl.project.model.test.SampleBoardInfo
import com.adl.project.ui.viewholder.MainSampleViewHolder


class MainSampleAdapter : RecyclerView.Adapter<MainSampleViewHolder>() {
    private var lstBoardInfo: ArrayList<SampleBoardInfo>? = null
    private var listener: AdapterClickListener? = null

    init {
        lstBoardInfo = ArrayList()
    }

    fun setItemClickListener(_listener: AdapterClickListener) {
        listener = _listener
    }

    fun setListInit(_lstBoardInfo : List<SampleBoardInfo>) {
        if (lstBoardInfo?.isNotEmpty() == true)
            lstBoardInfo?.clear()
        lstBoardInfo?.addAll(_lstBoardInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainSampleViewHolder {
        return MainSampleViewHolder(ListItemSampleMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainSampleViewHolder, position: Int) {
        holder.bind(lstBoardInfo!![position], listener)
    }

    override fun getItemCount(): Int {
        return lstBoardInfo?.size ?: 0
    }
}
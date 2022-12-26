package com.adl.project.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.databinding.ListItemMainLegendBinding
import com.adl.project.databinding.ListItemSampleMainBinding
import com.adl.project.model.test.SampleBoardInfo
import com.adl.project.ui.viewholder.MainLegendViewHolder
import com.adl.project.ui.viewholder.MainSampleViewHolder

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 메인 화면의 Location Legend 표시를 위한 리사이클러뷰 어댑터
 */

class MainLegendAdapter : RecyclerView.Adapter<MainLegendViewHolder>() {
    private var listener: AdapterClickListener? = null
    private var names: ArrayList<String>? = null
    private var colors: ArrayList<Int>? = null

    init {
        names = ArrayList()
        colors = ArrayList()
    }

    fun setItemClickListener(_listener: AdapterClickListener) {
        listener = _listener
    }

    fun setListInit(_colorMap : MutableMap<String, Int>) {
        for((k,v) in _colorMap){
            names!!.add(k)
            colors!!.add(v)
        }

        Log.d("DBG::RV_MAINLEGEND",names.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainLegendViewHolder {
        return MainLegendViewHolder(ListItemMainLegendBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainLegendViewHolder, position: Int) {
        holder.bind(names!![position], colors!![position], listener)
    }

    override fun getItemCount(): Int {
        return names?.size ?: 0
    }
}
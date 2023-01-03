package com.adl.project.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adl.project.common.listener.AdapterClickListener
import com.adl.project.databinding.ListItemAdlmvsDetailBinding
import com.adl.project.databinding.ListItemMainLegendBinding
import com.adl.project.model.adlmvs.AdlMvsListModel
import com.adl.project.model.adlmvs.AdlMvsModel
import com.adl.project.ui.viewholder.AdlMvsDetailViewHolder
import com.adl.project.ui.viewholder.MainLegendViewHolder

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: AdlMvs 디테일 화면의 데이터표시를 위한 리사이클러뷰 어댑터
 */

class AdlMvsDetailAdapter : RecyclerView.Adapter<AdlMvsDetailViewHolder>() {
    private var listener: AdapterClickListener? = null
    private var datas: ArrayList<AdlMvsModel>? = null

    init {
        datas = ArrayList()
    }

    fun setItemClickListener(_listener: AdapterClickListener) {
        listener = _listener
    }

    fun setListInit(_datas : List<AdlMvsModel>) {
        if (datas?.isNotEmpty() == true)
            datas?.clear()
        datas?.addAll(_datas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdlMvsDetailViewHolder {
        return AdlMvsDetailViewHolder(ListItemAdlmvsDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AdlMvsDetailViewHolder, position: Int) {
        val fromTime = datas!![position].fromTime
        val toTime = datas!![position].toTime
        val diffTime = datas!![position].timeDiff

        holder.bind(fromTime, toTime, diffTime, listener)
    }

    override fun getItemCount(): Int {
        Log.d("DBG::ADAPTER", datas!!.size.toString())
        return datas?.size ?: 0
    }
}
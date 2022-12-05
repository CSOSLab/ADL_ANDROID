package com.magical.near.ui.fragment

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.adl.project.R
import com.adl.project.databinding.FragmentAnalyticOneDetailBinding
import com.adl.project.ui.base.BaseFragment

/**
 * ADL_MONITORING_APP by CSOS PROJECT
 * DEVELOPER : 한병하 (Glacier Han)
 * TODO :: 분석결과 1화면의 메인 프래그먼트
 */

class AnalyticOneDetailFragment : BaseFragment<FragmentAnalyticOneDetailBinding>(FragmentAnalyticOneDetailBinding::bind, R.layout.fragment_analytic_one_detail), View.OnClickListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInitialize()
    }

    private fun setInitialize() {
    }

    override fun onClick(_view: View?) {
        when (_view?.id) {
//            R.id.btn_ -> {
//
//            }
        }
    }
}
package com.magical.near.ui.fragment

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class AnalyticOneDetailFragment : BaseFragment<FragmentAnalyticOneDetailBinding>(
    FragmentAnalyticOneDetailBinding::bind,
    R.layout.fragment_analytic_one_detail
), View.OnClickListener {

    val mode by lazy{
        arguments?.getString("mode").toString()
    }
    val name by lazy {
        arguments?.getString("name").toString()
    }
    val KEY by lazy {
        arguments?.getString("KEY").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgument()
        setInitialize()
    }

    private fun setInitialize() {

        binding.tvTest.setText(name + " :: " + KEY)
    }

    private fun getArgument() {


        Log.d("name", name + mode)

    }

    override fun onClick(_view: View?) {
        when (_view?.id) {
//            R.id.btn_ -> {
//
//            }
        }
    }
}
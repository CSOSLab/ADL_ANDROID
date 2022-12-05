package com.adl.project.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.adl.project.R
import com.adl.project.common.enum.TransitionMode
import com.adl.project.databinding.ActivityAnalyticOneDetailBinding
import com.adl.project.databinding.ActivityLoginBinding
import com.adl.project.ui.base.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.magical.near.ui.fragment.AnalyticOneDetailFragment

class AnalyticOneDetailActivity : BaseActivity<ActivityAnalyticOneDetailBinding>(ActivityAnalyticOneDetailBinding::inflate, TransitionMode.FADE) {

    val mViewPager by lazy {
        binding.pager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialize()
    }

    fun setInitialize(){
        setViewPager()
    }

    fun setViewPager(){
        mViewPager.adapter = PagerAdapter(supportFragmentManager)
        mViewPager.currentItem = 0

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        tabLayout.getTabAt(0)!!.text = "년"
        tabLayout.getTabAt(1)!!.text = "월"
        tabLayout.getTabAt(2)!!.text = "주"
        tabLayout.getTabAt(3)!!.text = "일"

        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = tab.select()

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    inner class PagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> AnalyticOneDetailFragment()
                1 -> AnalyticOneDetailFragment()
                2 -> AnalyticOneDetailFragment()
                3 -> AnalyticOneDetailFragment()
                else -> AnalyticOneDetailFragment()
            }
        }

        override fun getCount(): Int = 4
    }
}
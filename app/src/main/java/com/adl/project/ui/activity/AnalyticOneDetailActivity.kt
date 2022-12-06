package com.adl.project.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

class AnalyticOneDetailActivity : BaseActivity<ActivityAnalyticOneDetailBinding>(
    ActivityAnalyticOneDetailBinding::inflate,
    TransitionMode.FADE
) {

    val mViewPager by lazy {
        binding.pager
    }

    var mode = ""
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIntents()
        setInitialize()
    }

    fun setInitialize() {
        setViewPager()
        binding.tvTitle.setText(name)

    }

    fun getIntents() {
        intent.apply {
            if (extras != null) {
                mode = getStringExtra("mode").toString()
                name = getStringExtra("name").toString()
            } else {
                finish()
            }
        }
    }

    fun setViewPager() {
        mViewPager.adapter = PagerAdapter(supportFragmentManager, name, mode)
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

    inner class PagerAdapter(supportFragmentManager: FragmentManager, name_:String, mode_:String) :
        FragmentStatePagerAdapter(supportFragmentManager) {
        val name_ = name_
        val mode_ = mode_

        override fun getItem(position: Int): Fragment {

            return when (position) {
                // TODO 참고 :: https://medium.com/hongbeomi-dev/fragment-잘-써보기-bundle-c2fd8fe96967
                0 -> {
                    Log.d("name", name + mode)
                    AnalyticOneDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("KEY", "년도별 차트 보기")
                            putString("name", name_)
                            putString("mode", mode_)
                        }
                    }
                }
                1 -> {
                    AnalyticOneDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("KEY", "월별 차트 보기")
                            putString("name", name_)
                            putString("mode", mode_)
                        }
                    }
                }
                2 -> {
                    AnalyticOneDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("KEY", "주별 차트 보기")
                            putString("name", name_)
                            putString("mode", mode_)
                        }
                    }
                }
                3 -> {
                    AnalyticOneDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("KEY", "일별 차트 보기")
                            putString("name", name_)
                            putString("mode", mode_)
                        }
                    }
                }
                else -> {
                    AnalyticOneDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString("KEY", "일별 차트 보기")
                            putString("name", name_)
                            putString("mode", mode_)
                        }
                    }
                }
            }
        }

        override fun getCount(): Int = 4
    }
}
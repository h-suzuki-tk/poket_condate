package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import java.lang.ClassCastException


class TabFragment() : Fragment() {
    var mListener: OnRegisterCondateSelectedListener? = null

    companion object {
        fun newInstance(): TabFragment {
            val fragment = TabFragment()
            return fragment
        }
    }

    interface OnRegisterCondateSelectedListener {
        fun replaceFragment(fragment: Fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_tab_main, container, false)

        // タブとViewPagerをセット
        val tabsFragments = arrayListOf(
            TopFragment::class.java,
            CalendarFragment::class.java,
            EvaluationFragment::class.java
        )
        val tabLayout: TabLayout = v.findViewById(R.id.tab_layout)
        val container: ViewPager = v.findViewById(R.id.container)
        container.adapter = TabsPagerAdapter(childFragmentManager, tabsFragments)
        container.setCurrentItem(arguments?.getInt("PageIndex") ?: 0, false)
        tabLayout.setupWithViewPager(container)

        // 「献立を登録する !」ボタンの設定
        val addNewCondateButton: Button = v.findViewById(R.id.addNewCondate)
        addNewCondateButton.setOnClickListener {
            when (mListener) {
                null -> throw NullPointerException("mListener must be non-null")
                else -> {
                    val f: Fragment = CondateRegistrationFragment()
                    mListener!!.replaceFragment(f)
                }
            }
        }

        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        when (context) {
            is OnRegisterCondateSelectedListener -> this.mListener = context
            else -> throw ClassCastException("${context.toString()} must implement OnCellSelectedListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
    }


}
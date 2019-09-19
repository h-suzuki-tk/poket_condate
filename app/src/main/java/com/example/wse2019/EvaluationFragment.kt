package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import java.lang.ClassCastException


class EvaluationFragment() : Fragment() {

        companion object {
            fun newInstance(): EvaluationFragment {
                val fragment = EvaluationFragment()
                return fragment
            }
        }



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val v: View = inflater.inflate(R.layout.fragment_evaluation, container, false)

            // タブとViewPagerをセット
            val tabsFragments = arrayListOf(
                EvaluationDayFragment::class.java,
                EvaluationWeekFragment::class.java,
                EvaluationMonthFragment::class.java
            )
            val tabLayout: TabLayout = v.findViewById(R.id.tab_layout)
            val container: ViewPager = v.findViewById(R.id.container)
            container.adapter = EvaluationTabsPagerAdapter(childFragmentManager, tabsFragments)
            container.setCurrentItem(arguments?.getInt("PageIndex") ?: 0, false)
            tabLayout.setupWithViewPager(container)


            return v
        }
        override fun onAttach(context: Context?) {
            super.onAttach(context)

        }
        override fun onDetach() {
            super.onDetach()
        }
}
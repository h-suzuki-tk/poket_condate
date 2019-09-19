package com.example.wse2019

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class EvaluationTabsPagerAdapter(fm: FragmentManager,
                       tabsFragments : ArrayList<Class<out Fragment>>) : FragmentPagerAdapter(fm) {

    val tabsFragments: ArrayList<Class<out Fragment>> = tabsFragments
    val tabTitles: Array<CharSequence?> = arrayOf("日別", "週別", "月別")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment {
        return tabsFragments[position].newInstance()
    }

    override fun getCount(): Int {
        return tabsFragments.size
    }

}
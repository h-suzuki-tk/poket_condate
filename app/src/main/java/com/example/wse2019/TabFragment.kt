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


class TabFragment() : Fragment() {
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
            /* 動作をここに記述 */
            val f: Fragment = CondateRegistrationFragment()
            val ft: FragmentTransaction = fragmentManager?.beginTransaction() ?: throw java.lang.AssertionError("fragmentManager is null")
            ft.replace(R.id.frame_contents, f)
            ft.commit()
        }

        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }
    companion object {
        fun newInstance(): TabFragment {
            val fragment = TabFragment()
            return fragment
        }
    }
}
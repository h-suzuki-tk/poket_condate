package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

private const val SCROLL_OFFSET = 3

class CalendarFragment() : Fragment() {
    lateinit var listener: OnCellSelectedListener
    private val dm: DateManager = DateManager()

    // スクロール位置
    var sp = -1 // Scroll position
    var sy = -1 // Scroll Y coordinate

    companion object {
        fun newInstance(): CalendarFragment {
            val fragment = CalendarFragment()
            return fragment
        }
    }

    interface OnCellSelectedListener {
        fun replaceFragment(fragment: Fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // スクロール位置を初期化
        /*
         * 今日の日付が上からSCROLL_OFFSET行にくるよう設定
         */
        sp = dm.getDate(dm.calendar.time).toInt() - SCROLL_OFFSET
        sy = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        // ListView の処理
        val listView: ListView          = v.findViewById(R.id.calendarListView)
        val cAdapter: CalendarAdapter   = when (context) {
                null -> throw NullPointerException()
                else -> CalendarAdapter(context!!) }
        listView.apply {
            adapter = cAdapter
            setSelectionFromTop(sp, sy)
            setOnItemClickListener { adapterView, view, position, time ->
                val day: Date = cAdapter.getItem(position)

                // スクロール位置の保存
                sp = listView.firstVisiblePosition
                sy = listView.getChildAt(0).top

                listener.replaceFragment(CondateRegistrationFragment.newInstance(
                    dm.getYear(day) .toInt(),
                    dm.getMonth(day).toInt(),
                    dm.getDate(day) .toInt(),
                    time            .toInt()))
            }
        }

        // 当月の表示
        val currentYearMonth: TextView = v.findViewById(R.id.currentYearMonth)
        currentYearMonth.text = cAdapter.getCurrentYearMonth()
        // 先月ボタン・来月ボタンの処理
        val prevButton: Button = v.findViewById(R.id.prevMonth)
        val nextButton: Button = v.findViewById(R.id.nextMonth)
        prevButton.setOnClickListener {
            cAdapter.prevMonth()
            currentYearMonth.text = cAdapter.getCurrentYearMonth()
        }
        nextButton.setOnClickListener {
            cAdapter.nextMonth()
            currentYearMonth.text = cAdapter.getCurrentYearMonth()
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        when (context) {
            is OnCellSelectedListener -> this.listener = context
            else -> throw ClassCastException("${context.toString()} must implement OnCellSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

}
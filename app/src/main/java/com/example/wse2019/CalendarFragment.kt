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


class CalendarFragment() : Fragment() {
    var listener: OnCellSelectedListener? = null
    private val dm: DateManager = DateManager()
    private val calendar_selection_offset: Int = 3

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        // ListView の処理
        val listView: ListView = v.findViewById(R.id.calendarListView)
        var adapter: CalendarAdapter = if (context != null) CalendarAdapter(context!!) else throw AssertionError("Context is null.")
        listView.adapter = adapter
        listView.setSelection(dm.getDate(dm.calendar.time).toInt() - calendar_selection_offset)
        listView.setOnItemClickListener { _, _, position, time ->
            val day: Date = adapter.getItem(position)
            when (this.listener) {
                null -> throw NullPointerException()
                else -> {
                    val f: Fragment = CondateRegistrationFragment.newInstance(dm.getYear(day).toInt(), dm.getMonth(day).toInt(), dm.getDate(day).toInt(), time.toInt())
                    this.listener!!.replaceFragment(f)
                }
            }
        }

        // 当月の表示
        val currentYearMonth: TextView = v.findViewById(R.id.currentYearMonth)
        currentYearMonth.text = adapter.getCurrentYearMonth()
        // 先月ボタン・来月ボタンの処理
        val prevButton: Button = v.findViewById(R.id.prevMonth)
        val nextButton: Button = v.findViewById(R.id.nextMonth)
        prevButton.setOnClickListener {
            adapter.prevMonth()
            currentYearMonth.text = adapter.getCurrentYearMonth()
        }
        nextButton.setOnClickListener {
            adapter.nextMonth()
            currentYearMonth.text = adapter.getCurrentYearMonth()
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
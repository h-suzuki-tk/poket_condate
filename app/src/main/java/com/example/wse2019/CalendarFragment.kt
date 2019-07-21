package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*


class CalendarFragment() : Fragment() {
    var mListener: OnCellSelectedListener? = null

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
        var adapter: CalendarAdapter = if (context != null) CalendarAdapter(context!!) else throw AssertionError("Content is null.")
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val mDate: Date = adapter.getItem(position)
            val year: Int = SimpleDateFormat("yyyy", Locale.JAPAN).format(mDate).toInt()
            val month: Int = SimpleDateFormat("M", Locale.JAPAN).format(mDate).toInt()
            val date: Int = SimpleDateFormat("d", Locale.JAPAN).format(mDate).toInt()
            var time: String = ""
            when (id) {
                R.id.morningListView.toLong() -> time = "朝"
                R.id.noonListView.toLong() -> time = "昼"
                R.id.eveningListView.toLong() -> time = "晩"
                R.id.snackListView.toLong() -> time = "間食"
            }
            when (mListener) {
                null -> throw NullPointerException("mListener must be non-null")
                else -> {
                    val f: Fragment = CondateRegistrationFragment.newInstance(year, month, date, time)
                    mListener!!.replaceFragment(f)
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
            is OnCellSelectedListener -> this.mListener = context
            else -> throw ClassCastException("${context.toString()} must implement OnCellSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

}
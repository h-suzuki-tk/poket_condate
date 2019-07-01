package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView


class CalendarFragment() : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        val prevButton: Button = v.findViewById(R.id.prevMonth)
        val nextButton: Button = v.findViewById(R.id.nextMonth)
        prevButton.setOnClickListener { /* 動作をここに記述 */ }
        nextButton.setOnClickListener { /* 動作をここに記述 */ }

        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }
    companion object {
        fun newInstance(): CalendarFragment {
            val fragment = CalendarFragment()
            return fragment
        }
    }
}
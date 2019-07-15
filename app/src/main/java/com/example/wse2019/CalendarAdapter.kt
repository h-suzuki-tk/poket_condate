package com.example.wse2019

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarAdapter(context: Context) : BaseAdapter() {

    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val dateManager: DateManager = DateManager()

    var dateArray: ArrayList<Date> = dateManager.getDays()

    override fun getCount(): Int {
        return dateArray.size
    }

    override fun getItem(position: Int): Any {
        return dateArray[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = inflater.inflate(R.layout.calendar_row, parent, false)

        v.findViewById<TextView>(R.id.date).text = SimpleDateFormat("d", Locale.JAPAN).format(dateArray[position])

        return v
    }

    fun getCurrentYearMonth(): String {
        return SimpleDateFormat("yyyy/MM", Locale.JAPAN).format(dateManager.calendar.time)
    }

    fun nextMonth() {
        dateManager.nextMonth()
        dateArray = dateManager.getDays()
        this.notifyDataSetChanged()
    }

    fun prevMonth() {
        dateManager.prevMonth()
        dateArray = dateManager.getDays()
        this.notifyDataSetChanged()
    }

}
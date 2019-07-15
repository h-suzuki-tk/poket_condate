package com.example.wse2019

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView


class CalendarAdapter(context: Context) : BaseAdapter() {

    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var foodList: ArrayList<Calendar> = arrayListOf<Calendar>()

    override fun getCount(): Int {
        return foodList.size
    }

    override fun getItem(position: Int): Any {
        return foodList[position]
    }

    override fun getItemId(position: Int): Long {
        return foodList[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = inflater.inflate(R.layout.calendar_row, parent, false)

        v.findViewById<TextView>(R.id.name).text = foodList[position].name
        v.findViewById<TextView>(R.id.price).text = foodList[position].price.toString()

        return v
    }

}
package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import java.lang.IllegalArgumentException


class CalendarFragment() : Fragment() {

    // (仮) ListView に表示する項目
    private val foods = arrayOf(
        "りんご", "ばなな", "パイナップル", "いちご"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        // 先月ボタン・来月ボタンの処理
        val prevButton: Button = v.findViewById(R.id.prevMonth)
        val nextButton: Button = v.findViewById(R.id.nextMonth)
        prevButton.setOnClickListener { /* 動作をここに記述 */ }
        nextButton.setOnClickListener { /* 動作をここに記述 */ }

        // ListView の処理
        val listView: ListView = v.findViewById(R.id.calendarListView)
        var list: ArrayList<Calendar> = ArrayList<Calendar>()
        var adapter: CalendarAdapter = if (context != null) CalendarAdapter(context!!) else throw AssertionError()
        adapter.foodList = list
        listView.adapter = adapter
        val food: Calendar = Calendar()
        food.name = "りんご"
        food.price = 100
        list.add(food)
        adapter.notifyDataSetChanged()
        val food2: Calendar = Calendar()
        food2.name = "みかん"
        food2.price = 70
        list.add(food2)
        adapter.notifyDataSetChanged()

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
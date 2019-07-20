package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.w3c.dom.Text
import java.lang.AssertionError
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarAdapter(context: Context) : BaseAdapter() {

    val context: Context = context
    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val dateManager: DateManager = DateManager()

    // 1 行分の各セルの内容
    /*
     * 現在は仮の固定データをいれてある
     *
     * getView で各行 (各日) に対する表示処理ができるので
     * そこで DB 検索・下記配列にプッシュしていけばよいと思われる
     */
    var morning: ArrayList<String> = arrayListOf("やきそば", "牛乳")
    var noon: ArrayList<String> = arrayListOf("焼肉", "コーンポタージュ", "りんご", "みかん", "ぶどう")
    var evening: ArrayList<String> = arrayListOf("ジュース")
    var snack: ArrayList<String> = arrayListOf("ケーキ", "お好み焼き")

    var dateArray: ArrayList<Date> = dateManager.getDays()

    override fun getCount(): Int {
        return dateArray.size
    }

    override fun getItem(position: Int): Date {
        return dateArray[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = inflater.inflate(R.layout.calendar_row, parent, false)

        // DB 検索・配列に追加処理
        // setCondate(position)

        val parent: ListView = parent as ListView

        v.findViewById<TextView>(R.id.date).text = SimpleDateFormat("d", Locale.JAPAN).format(dateArray[position])
        val morningListView: ListView = v.findViewById(R.id.morningListView)
        val noonListView: ListView = v.findViewById(R.id.noonListView)
        val eveningListView: ListView = v.findViewById(R.id.eveningListView)
        val snackListView: ListView = v.findViewById(R.id.snackListView)
        morningListView.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, morning)
        noonListView.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, noon)
        eveningListView.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, evening)
        snackListView.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, snack)
        morningListView.setOnTouchListener { view, _ ->
            parent.performItemClick(view, position, R.id.morningListView.toLong())
            true
        }
        noonListView.setOnTouchListener { view, _ ->
            parent.performItemClick(view, position, R.id.noonListView.toLong())
            true
        }
        eveningListView.setOnTouchListener { view, _ ->
            parent.performItemClick(view, position, R.id.eveningListView.toLong())
            true
        }
        snackListView.setOnTouchListener { view, _ ->
            parent.performItemClick(view, position, R.id.snackListView.toLong())
            true
        }

        return v
    }

    fun setCondate(position: Int) {

        morning.clear()
        noon.clear()
        evening.clear()
        snack.clear()

        /* ここに処理を記述 */

    }

    fun getCurrentYearMonth(): String {
        return SimpleDateFormat("yyyy/M", Locale.JAPAN).format(dateManager.calendar.time)
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
package com.example.wse2019

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
    var morningCondate: ArrayList<String> = arrayListOf("やきそば", "牛乳")
    var noon: ArrayList<String> = arrayListOf("焼肉", "コーンポタージュ", "りんご", "みかん", "ぶどう")
    var evening: ArrayList<String> = arrayListOf("ジュース")
    var snack: ArrayList<String> = arrayListOf("ケーキ", "お好み焼き")

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

        // DB 検索・配列に追加処理
        // setCondate(position)

        val parent: ListView = parent as ListView
        val morning: ListView = v.findViewById(R.id.morningListView)
        morning.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, morningCondate)
        //morning.setOnClickListener { if (parent != null) parent.performItemClick(v, position, R.id.morningListView.toLong()) else throw AssertionError("parent is null") }
        v.findViewById<FrameLayout>(R.id.morningLayout).setOnClickListener {
            Toast.makeText(context, "おしましたね！", Toast.LENGTH_LONG).show()
            if (parent != null) parent.performItemClick(v, position, R.id.morningListView.toLong()) else throw AssertionError("parent is null") }

        v.findViewById<TextView>(R.id.date).text = SimpleDateFormat("d", Locale.JAPAN).format(dateArray[position])
        //v.findViewById<ListView>(R.id.morningListView).adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, morning)
        v.findViewById<ListView>(R.id.noonListView).adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, noon)
        v.findViewById<ListView>(R.id.eveningListView).adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, evening)
        v.findViewById<ListView>(R.id.snackListView).adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, snack)

        return v
    }

    fun setCondate(position: Int) {

        morningCondate.clear()
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
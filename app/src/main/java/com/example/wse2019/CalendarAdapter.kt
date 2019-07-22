package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.calendar_row.view.*
import org.w3c.dom.Text
import java.lang.AssertionError
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarAdapter(context: Context) : BaseAdapter() {

    enum class Time(val listViewId: Int, val areaId: Int, var condate: ArrayList<String>) {
        MORNING(R.id.morningListView, R.id.morningArea, arrayListOf()),
        NOON(R.id.noonListView, R.id.noonArea, arrayListOf()),
        EVENING(R.id.eveningListView, R.id.eveningArea, arrayListOf()),
        SNACK(R.id.snackListView, R.id.snackArea, arrayListOf())
    }

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
        val parent: ListView = parent as ListView

        // 日付のセット
        val date: TextView = v.findViewById(R.id.date)
        date.text = SimpleDateFormat("d", Locale.US).format(dateArray[position])

        // 仮にデータをセット
        /*
         * SetCondate() 処理を実装後、削除
         */
        Time.MORNING.condate = this.morning
        Time.NOON.condate = this.noon
        Time.EVENING.condate = this.evening
        Time.SNACK.condate = this.snack

        // 各セルのセット
        Time.values().forEach { time ->
            val listView: ListView = v.findViewById(time.listViewId)
            val area: FrameLayout = v.findViewById(time.areaId)
            /* setCondate(position) */
            listView.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, time.condate)
            area.setOnClickListener { parent.performItemClick(v, position, time.listViewId.toLong()) }
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
        return SimpleDateFormat("yyyy/M", Locale.US).format(dateManager.calendar.time)
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
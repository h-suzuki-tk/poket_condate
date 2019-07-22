package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import kotlinx.android.synthetic.main.calendar_row.view.*
import org.w3c.dom.Text
import java.lang.AssertionError
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class CalendarAdapter(context: Context) : BaseAdapter() {

    enum class Time(
        val id: Int,
        val listViewId: Int,
        val areaId: Int)
    {
        MORNING (0, R.id.morningListView,   R.id.morningArea),
        NOON    (1, R.id.noonListView,      R.id.noonArea),
        EVENING (2, R.id.eveningListView,   R.id.eveningArea),
        SNACK   (3, R.id.snackListView,     R.id.snackArea)
    }

    val context: Context = context
    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val dm: DateManager = DateManager()
    var days: ArrayList<Date> = dm.getDays()

    override fun getCount(): Int {
        return days.size
    }

    override fun getItem(position: Int): Date {
        return days[position]
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
        date.text = dm.getDate(days[position])

        // 各セルのセット
        Time.values().forEach { time ->
            val listView: ListView = v.findViewById(time.listViewId)
            val area: FrameLayout = v.findViewById(time.areaId)
            val condate: List<String> = getCondate(days[position], time.id)
            listView.adapter = ArrayAdapter<String>(context, R.layout.calendar_cell, condate)
            area.setOnClickListener { parent.performItemClick(v, position, time.id.toLong()) }
        }

        return v
    }

    // ------------------------------------------------------------
    //  getCondate - その日の献立をセット
    // ------------------------------------------------------------
    /*
    position で与えられた日付の献立を condate にセットする
     */
    fun getCondate(day: Date, time: Int): List<String> {
        val db = SampleDBOpenHelper(context).readableDatabase
        var result: MutableList<String> = mutableListOf()

        val query: String =
            "select foods.name " +
                "from foods inner join records " +
                    "on records.year == ${dm.getYear(day)} and records.month == ${dm.getMonth(day)} and records.date == ${dm.getDate(day)} and records.time == ${time} and foods.id == records.food_id"
        val cursor: Cursor = db.rawQuery(query, null) ?: throw SQLiteException()
        while (cursor.moveToNext()) {
            val food: String? = cursor.getString(cursor.getColumnIndex(DBContract.Food.NAME))
            if (food != null) { result.add(food) }
        }

        return result
    }

    fun getCurrentYearMonth(): String {
        return SimpleDateFormat("yyyy/M", Locale.US).format(dm.calendar.time)
    }

    fun nextMonth() {
        dm.nextMonth()
        days = dm.getDays()
        this.notifyDataSetChanged()
    }

    fun prevMonth() {
        dm.prevMonth()
        days = dm.getDays()
        this.notifyDataSetChanged()
    }

}
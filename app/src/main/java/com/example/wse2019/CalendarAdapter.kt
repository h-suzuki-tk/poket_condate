package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Time
import java.text.SimpleDateFormat
import java.util.*
import kotlin.AssertionError
import kotlin.collections.ArrayList


class CalendarAdapter(val context: Context) : BaseAdapter() {
    val dm: DateManager = DateManager()

    // 各行で表示する情報
    private data class ViewHolderItem(
        val date    : TextView,
        val morning : ListView,
        val noon    : ListView,
        val evening : ListView,
        val snack   : ListView,
        val area_morning    : FrameLayout,
        val area_noon       : FrameLayout,
        val area_evening    : FrameLayout,
        val area_snack      : FrameLayout
    )

    // 表示する日付
    /*
     * ArrayList(Date)
     */
    var days: ArrayList<Date> = dm.getDays()

    // 表示する各日時の献立内容
    /*
     * MutableMap(Pair(Date<Int>, Time<Int>) to FoodNameList<MutableList<String>>)
     */
    var condate: MutableMap<Pair<Int, Int>, MutableList<String>> = mutableMapOf()

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
        val p: ListView = parent as ListView

        // ビューホルダーの設定
        val (viewHolder, view) = when (convertView) {
            null -> {
                val view = LayoutInflater.from(context).inflate(R.layout.calendar_row, parent, false)
                val viewHolder = ViewHolderItem(
                    date    = view.findViewById(R.id.date),
                    morning = view.findViewById(R.id.morningListView),
                    noon    = view.findViewById(R.id.noonListView),
                    evening = view.findViewById(R.id.eveningListView),
                    snack   = view.findViewById(R.id.snackListView),
                    area_morning    = view.findViewById(R.id.morningArea),
                    area_noon       = view.findViewById(R.id.noonArea),
                    area_evening    = view.findViewById(R.id.eveningArea),
                    area_snack      = view.findViewById(R.id.snackArea)
                )
                view.tag = viewHolder
                viewHolder to view
            }
            else -> convertView.tag as ViewHolderItem to convertView
        }

        // 登録済み献立をセット
        val c: MutableList<List<String>> = mutableListOf()
        Time.OBJECT.forEach {
            c += condate[Pair(dm.getDate(days[position]).toInt(), it)] ?: throw AssertionError()
        }

        // ビューの設定
        viewHolder.apply {

            // 日付のセット
            date.apply {
                text = dm.getDate(days[position])
            }

            // 各セルのセット
            viewHolder.apply {
                morning .apply {
                    adapter = ArrayAdapter(context, R.layout.calendar_cell, c[Time.MORNING])
                }
                noon    .apply {
                    adapter = ArrayAdapter(context, R.layout.calendar_cell, c[Time.NOON])
                }
                evening .apply {
                    adapter = ArrayAdapter(context, R.layout.calendar_cell, c[Time.EVENING])
                }
                snack   .apply {
                    adapter = ArrayAdapter(context, R.layout.calendar_cell, c[Time.SNACK])
                }
                area_morning.apply {
                    setOnClickListener { p.performItemClick(view, position, Time.MORNING.toLong()) }
                }
                area_noon   .apply {
                    setOnClickListener { p.performItemClick(view, position, Time.NOON.toLong()) }
                }
                area_evening.apply {
                    setOnClickListener { p.performItemClick(view, position, Time.EVENING.toLong()) }
                }
                area_snack  .apply {
                    setOnClickListener { p.performItemClick(view, position, Time.SNACK.toLong()) }
                }
            }

        }

        return view
    }



    // ------------------------------------------------------------
    //  updateCondateContents
    //  - 献立内容を最新のものに更新
    // ------------------------------------------------------------
    fun updateCondateContents() {
        condate = getMonthlyCondate(dm.getYear(dm.calendar.time).toInt(), dm.getMonth(dm.calendar.time).toInt())
        notifyDataSetChanged()
    }



    // --------------------------------------------------
    //  getMonthlyCondate
    //  - 該当月に登録されている献立を取得
    // --------------------------------------------------
    /*
     * @params
     *      Year    : year<Int>
     *      Month   : month<Int>
     * @return
     *      MutableMap(Pair(Date<Int>, Time<Int>) to FoodNameList<List<String>>)
     */
    private fun getMonthlyCondate(year: Int, month: Int): MutableMap<Pair<Int, Int>, MutableList<String>> {
        val condate: MutableMap<Pair<Int, Int>, MutableList<String>> = mutableMapOf()

        // 検索の準備
        val dm = DateManager().apply {
            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month.dec())
            }
        }
        val db = SampleDBOpenHelper(context)
        val foodT   = DBContract.Food
        val recordT = DBContract.Record

        // 検索
        val tmp = db.searchRecord(
            tableName = foodT.TABLE_NAME,
            column = arrayOf(
                "${recordT.TABLE_NAME}.${recordT.DATE}",
                "${recordT.TABLE_NAME}.${recordT.TIME}",
                "${foodT.TABLE_NAME}.${foodT.NAME}"),
            condition = "${recordT.TABLE_NAME}.${recordT.YEAR} = ${year}" +
                    " and ${recordT.TABLE_NAME}.${recordT.MONTH} = ${month}",
            innerJoin = Join(
                tablename = recordT.TABLE_NAME,
                column1 = foodT.ID,
                column2 = recordT.FOOD_ID
            )
        ) ?: throw NullPointerException()

        // 該当日時の献立の初期化
        dm.getDays().forEach { day ->
            val date = dm.getDate(day).toInt()
            Time.OBJECT.forEach { time ->
                condate[Pair(date, time)] = mutableListOf()
            }
        }

        // 格納
        var i = 0
        while (i < tmp.size) {
            val date        = tmp[i++].toInt()
            val time        = tmp[i++].toInt()
            val foodName    = tmp[i++]
            condate[Pair(date, time)]?.add(foodName) ?: throw NullPointerException()
        }

        return condate
    }


    // ------------------------------------------------------------
    //  getCurrentYearMonth
    //  - 現在表示している年月を取得
    // ------------------------------------------------------------
    /*
     * @return <String>yyyy/M (ex. 2019/9)
     */
    fun getCurrentYearMonth(): String {
        return SimpleDateFormat("yyyy/M", Locale.US).format(dm.calendar.time)
    }



    // ------------------------------------------------------------
    //  nextMonth
    //  - カレンダーのデータを翌月のものに更新
    // ------------------------------------------------------------
    fun nextMonth() {
        dm.nextMonth()
        days = dm.getDays()
        condate = getMonthlyCondate(dm.getYear(dm.calendar.time).toInt(), dm.getMonth(dm.calendar.time).toInt())
        this.notifyDataSetChanged()
    }



    // ------------------------------------------------------------
    //  prevMonth
    //  - カレンダーのデータを先月のものに更新
    // ------------------------------------------------------------
    fun prevMonth() {
        dm.prevMonth()
        days = dm.getDays()
        condate = getMonthlyCondate(dm.getYear(dm.calendar.time).toInt(), dm.getMonth(dm.calendar.time).toInt())
        this.notifyDataSetChanged()
    }

}
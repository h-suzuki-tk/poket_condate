package com.example.wse2019


import android.app.AlertDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

private const val SCROLL_OFFSET = 3

class CalendarFragment() : Fragment() {
    lateinit var cAdapter: CalendarAdapter
    private val cm: CalendarManager = CalendarManager()

    // 表示位置
    lateinit var calendar: java.util.Calendar
    var sp = -1 // Scroll position
    var sy = -1 // Scroll Y coordinate

    companion object {
        fun newInstance(): CalendarFragment {
            val fragment = CalendarFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // スクロール位置を初期化
        /*
         * 今日の日付が上からSCROLL_OFFSET行にくるよう設定
         */
        calendar = cAdapter.cm.calendar
        sp = cm.getDate(cm.calendar.time).toInt() - SCROLL_OFFSET
        sy = 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_calendar, container, false)

        // 献立内容を最新に更新
        updateCondateContents()

        // ListView の処理
        val listView: ListView = v.findViewById(R.id.calendarListView)
        listView.apply {
            adapter = cAdapter.apply {
                cm.calendar = calendar
            }
            setSelectionFromTop(sp, sy)
            setOnItemClickListener { adapterView, view, position, time ->
                val day: Date = cAdapter.getItem(position)

                // 表示位置の保存
                calendar = cm.calendar
                sp = listView.firstVisiblePosition
                sy = listView.getChildAt(0).top

                // 画面遷移
                AlertDialog.Builder(context).apply {
                    setView(inflater.inflate(R.layout.calendar_cell_click_dialog, null))
                }.show().run {
                    findViewById<Button>(R.id.cccd_confirm_btn).run {
                        // 確認ボタンが押された場合、献立確認画面へ
                        setOnClickListener {
                            (activity as? MainActivity)?.replaceFragment(CondateConfirmationFragment.newInstance(
                                cm.getYear(day) .toInt(),
                                cm.getMonth(day).toInt(),
                                cm.getDate(day) .toInt(),
                                time            .toInt())
                            )
                            dismiss()
                        }
                    }
                    findViewById<Button>(R.id.cccd_edit_btn).run {
                        // 編集ボタンが押された場合、献立登録画面へ
                        setOnClickListener {
                            (activity as? MainActivity)?.replaceFragment(CondateRegistrationFragment.newInstance(
                                cm.getYear(day) .toInt(),
                                cm.getMonth(day).toInt(),
                                cm.getDate(day) .toInt(),
                                time            .toInt())
                            )
                            dismiss()
                        }
                    }
                }

            }
        }

        // 当月の表示
        val currentYearMonth: TextView = v.findViewById(R.id.currentYearMonth)
        currentYearMonth.text = cAdapter.getCurrentYearMonth()
        // 先月ボタン・来月ボタンの処理
        val prevButton: Button = v.findViewById(R.id.prevMonth)
        val nextButton: Button = v.findViewById(R.id.nextMonth)
        prevButton.setOnClickListener {
            cAdapter.prevMonth()
            currentYearMonth.text = cAdapter.getCurrentYearMonth()
        }
        nextButton.setOnClickListener {
            cAdapter.nextMonth()
            currentYearMonth.text = cAdapter.getCurrentYearMonth()
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        cAdapter = when (context) {
            null -> throw NullPointerException()
            else -> CalendarAdapter(context) }
    }

    override fun onDetach() {
        super.onDetach()
    }


    // ------------------------------------------------------------
    //  updateCondateContents
    //  - 献立内容を最新に更新
    // ------------------------------------------------------------
    private fun updateCondateContents() {
        cAdapter.updateCondateContents()
    }

}
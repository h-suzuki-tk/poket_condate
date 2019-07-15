package com.example.wse2019

import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import kotlin.collections.ArrayList

class DateManager() {

    val calendar: Calendar = Calendar.getInstance()

    // 当月の要素を取得
    fun getDays(): ArrayList<Date> {

        var days: ArrayList<Date> = ArrayList<Date>()
        val startDate: Date = calendar.time
        val dateNum: Int = calendar.getActualMaximum(Calendar.DATE)

        calendar.set(Calendar.DATE, 1)
        for (i: Int in 1..dateNum) {
            days.add(calendar.time)
            calendar.add(Calendar.DATE, 1)
        }

        calendar.time = startDate

        return days
    }

    // 翌月へ
    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
    }

    // 昨月へ
    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
    }

}
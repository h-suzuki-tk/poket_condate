package com.example.wse2019

import com.example.sample.Time

class DayManager {

    companion object {
        const val DEFAULT     = -1
        const val MORNING_STR = "朝"
        const val NOON_STR    = "昼"
        const val EVENING_STR = "晩"
        const val SNACK_STR   = "間"
    }

    private var m_year  : Int
    private var m_month : Int
    private var m_date  : Int
    private var m_time  : Int

    init {
        m_year  = DEFAULT
        m_month = DEFAULT
        m_date  = DEFAULT
        m_time  = DEFAULT
    }

    val year
        get() = m_year
    val month
        get() = m_month
    val date
        get() = m_date
    val time
        get() = m_time

    val strTime
        get() = when (time) {
            Time.MORNING -> MORNING_STR
            Time.NOON    -> NOON_STR
            Time.EVENING -> EVENING_STR
            Time.SNACK   -> SNACK_STR
            else -> throw AssertionError()
        }

    fun set(
        year  : Int = DEFAULT,
        month : Int = DEFAULT,
        date  : Int = DEFAULT,
        time  : Int = DEFAULT
    ) {
        if (year  != DEFAULT) { m_year   = year }
        if (month != DEFAULT) { m_month  = month }
        if (date  != DEFAULT) { m_date   = date }
        if (time  != DEFAULT) {
            if (!Time.OBJECT.contains(time)) { throw AssertionError(
                "time == ${time} : Variable \"time\"'s value is uncorrect.")
            } else { m_time   = time }
        }
    }

    fun contains(any: Any?) : Boolean {
        return (m_year == any
                || m_month == any
                || m_date  == any
                || m_time  == any)
    }

    fun containsEmpty() : Boolean {
        return (m_year == DEFAULT
                || m_month == DEFAULT
                || m_date  == DEFAULT
                || m_time  == DEFAULT)
    }

}

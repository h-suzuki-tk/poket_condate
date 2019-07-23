package com.example.wse2019

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.lang.AssertionError
import java.sql.Date

class CondateRegistrationFragment(): Fragment() {

    var year: Int = -1
    var month: Int = -1
    var date: Int = -1
    var time: Int = -1

    companion object {
        fun newInstance(year: Int, month: Int, date: Int, time: Int) : CondateRegistrationFragment {
            val f = CondateRegistrationFragment()
            val args = Bundle()
            args.putInt("year", year)
            args.putInt("month", month)
            args.putInt("date", date)
            args.putInt("time", time)
            f.arguments = args
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: Bundle? = arguments
        when {
            args != null -> {
                this.year = args.getInt("year")
                this.month = args.getInt("month")
                this.date = args.getInt("date")
                this.time = args.getInt("time")
            }
        }
        val timeStr: String =
            when(time) {
                0 -> "朝"
                1 -> "昼"
                2 -> "晩"
                3 -> "間"
                -1 -> "指定なし"
                else -> throw AssertionError()
            }
        Toast.makeText(activity, "${this.year}年${this.month}月${this.date}日の${timeStr}の献立をおしました", Toast.LENGTH_SHORT).show()

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_condate_registration, container, false)



        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }

}
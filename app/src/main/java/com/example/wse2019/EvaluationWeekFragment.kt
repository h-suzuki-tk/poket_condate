package com.example.wse2019

import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.graphics.Typeface
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.formatter.*
import com.github.mikephil.charting.components.*
import java.util.Calendar
import android.text.format.*
import android.widget.Spinner
import android.widget.TextView
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.*
import kotlin.collections.ArrayList


class EvaluationWeekFragment() : Fragment(),OnChartValueSelectedListener {

    private val period =7
    private  val span =1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_evaluation_week, container, false)


        val db = SampleDBOpenHelper(requireContext())
        val nut=NutritionHelper(context)
        val mChart=v.findViewById<LineChart>(R.id.lineChart)
        val rChart=v.findViewById<RadarChart>(R.id.radarChart)
        val dateSpinner=v.findViewById<Spinner>(R.id.spinner)
        val textView2=v.findViewById<TextView>(R.id.textView2)
        val score=v.findViewById<TextView>(R.id.textView8)
        var dt=DateTime.now()




        val spinnerItems= ArrayList<String>()
        var item=""
        var text=""

        var i=0

        while(i<period) {

            dt=DateTime.now()
            dt.addDays(7*(i-3))//週の計算
            Log.d("date",dt.toString())
            //第何周かどうかで場合分け
            if(dt.day>0 && dt.day<=7){
                item=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第一週"
            }else if(dt.day>7 && dt.day<=14){
                item=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第二週"
            }else if(dt.day>14 && dt.day<=21){
                item=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第三週"
            }else if(dt.day>21 && dt.day<=28){
                item=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第四週"
            }else if(dt.day>28 && dt.day<=31){
                item=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第五週"
            }else{
                TODO()
            }
            spinnerItems.add(item.format())
            i++
        }



        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        dateSpinner.adapter=adapter
        dateSpinner.setSelection(3)

        // リスナーを登録
        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //　アイテムが選択された時
            override fun onItemSelected(parent: AdapterView<*>?,
                                        view: View?, position: Int, id: Long) {
                dt=DateTime.now().addDays(7*(-3))
                dt.addDays(7*position)
                if(dt.day>0 && dt.day<=7){
                    text=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第一週の詳細データ"
                }else if(dt.day>7 && dt.day<=14){
                    text=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第二週の詳細データ"
                }else if(dt.day>14 && dt.day<=21){
                    text=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第三週の詳細データ"
                }else if(dt.day>21 && dt.day<=28){
                    text=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第四週の詳細データ"
                }else if(dt.day>28 && dt.day<=31){
                    text=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第五週の詳細データ"
                }else{
                    TODO()
                }

                setUpRadarChart(rChart,dt,db,span,requireContext())
                textView2.setText(text)
                score.setText(nut.recordScore(dt.year,dt.month+1,dt.day,span).toString())
                Log.d("position",position.toString())
            }

            //　アイテムが選択されなかった
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
        }



        setupLineChart(mChart,span)
        mChart.data=lineDataWithCount(dt,requireContext(),span)






        return v
    }

    companion object {
        fun newInstance(): EvaluationFragment {
            val fragment = EvaluationFragment()
            return fragment
        }
    }



    override fun onValueSelected(e: Entry, h: Highlight) {
            Log.i("Entry selected", e.toString())
    }

    override fun onNothingSelected() {
            Log.i("Nothing selected", "Nothing selected.")
    }



}


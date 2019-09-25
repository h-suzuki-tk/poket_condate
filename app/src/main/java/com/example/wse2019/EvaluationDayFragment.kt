package com.example.wse2019

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.sample.SampleDBOpenHelper
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart

class EvaluationDayFragment() : Fragment() {

    private val period =7
    private  val span =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v= inflater.inflate(R.layout.fragment_evaluation_day, container, false)

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
            dt.addDays(i-3)//日の計算
            item=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+dt.day.toString()+"日"
            Log.d("date",dt.toString())
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
                dt=DateTime.now()
                setupLineChart(mChart,span)
                mChart.data=lineDataWithCount(dt,requireContext(),span)

                dt.addDays((-3))
                dt.addDays(position)
                text=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+dt.day.toString()+"日の詳細データ"

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





        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }
    companion object {
        fun newInstance(): EvaluationFragment {
            val fragment = EvaluationFragment()
            return fragment
        }
    }
}
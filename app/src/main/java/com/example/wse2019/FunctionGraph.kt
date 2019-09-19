package com.example.wse2019

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.format.DateFormat
import android.util.Log
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun setupLineChart(mChart: LineChart,span: Int){

    var mTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    val period =7

    // Grid背景色
    mChart.setDrawGridBackground(true)


    val xAxis = mChart.getXAxis()
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
    xAxis.typeface=mTypeface
    xAxis.setDrawLabels(true)
    xAxis.setDrawGridLines(true)
    xAxis.textSize=3f
    var i=0
    val labels=ArrayList<String>()
    var dt=DateTime.now()
    var label=""
    if (span == 0) {
        while (i < period) {

            dt = DateTime.now()
            dt.addDays(i-3)//日の計算
            label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+dt.day.toString()+"日"
            Log.d("date", dt.toString())
            labels.add(label.format())
            i++
        }
    }

    // 週別の場合
    else if (span == 1) {

        while(i<period) {

            dt=DateTime.now()
            dt.addDays(7*(i-3))//週の計算
            Log.d("date",dt.toString())
            //第何周かどうかで場合分け
            if(dt.day>0 && dt.day<=7){
                label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第一週"
            }else if(dt.day>7 && dt.day<=14){
                label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第二週"
            }else if(dt.day>14 && dt.day<=21){
                label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第三週"
            }else if(dt.day>21 && dt.day<=28){
                label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第四週"
            }else if(dt.day>28 && dt.day<=31){
                label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"+"第五週"
            }else{
                TODO()
            }
            labels.add(label.format())
            i++
        }
    }

    // 月別の場合
    else if (span == 2) {
        while (i < period) {

            dt = DateTime.now()
            dt.addMonths(i-3)//月の計算
            Log.d("date", dt.toString())
            label=dt.year.toString()+"年"+(dt.month+1).toString()+"月"
            labels.add(label.format())
            i++
        }
    } else {
        println("invalid span")
        println("日別:span=0、週別:span=1、月別:span=2")
        TODO()
    }

    xAxis.setValueFormatter(IndexAxisValueFormatter(labels))


    val leftAxis = mChart.getAxisLeft()
    leftAxis.typeface=mTypeface
    leftAxis.textColor=Color.BLACK
    leftAxis.setDrawGridLines(true)

    // Y軸最大最小設定
    leftAxis.setAxisMaximum(100f)
    leftAxis.setAxisMinimum(0f)

    // 右側の目盛り
    mChart.getAxisRight().setEnabled(false)

    mChart.setFocusable(false)
    mChart.setPinchZoom(false)
    mChart.setDoubleTapToZoomEnabled(false)
    mChart.setDrawGridBackground(false)
    mChart.setDescription(null)
    mChart.getLegend().setEnabled(false)
    mChart.animateY(2000, Easing.EaseInCubic)
    mChart.invalidate()



}

fun lineDataWithCount(dt:DateTime,context:Context,span:Int): LineData {

    val values = mutableListOf<Entry>()
    val Nut = NutritionHelper(context)
    val period=7


    for (i in 0 until period) {
        //現在は仮の乱数データを入れているが、関数が完成次第こちらに処理を入れる予定
        var value=0f

        if(span==0){
            value = Nut.recordScore(dt.year, dt.month+1, dt.day+i, span)?: continue
        }else if(span==1){
            value = Nut.recordScore(dt.year, dt.month+1, dt.day+i*7, span)?: continue

        }else if(span==2){
            value = Nut.recordScore(dt.year, dt.month+1+i, dt.day, span)?: continue
        }


        if(value==null){
            value=0f
        }

        values.add(Entry(i.toFloat(), value))
    }
    // create a dataset and give it a type
    val yVals = LineDataSet(values, "SampleLineData").apply {
        axisDependency =  YAxis.AxisDependency.LEFT
        color = Color.BLACK
        highLightColor = Color.YELLOW
        setDrawCircles(false)
        setDrawCircleHole(false)
        setDrawValues(false)
        lineWidth = 2f
    }
    val data = LineData(yVals)
    data.setValueTextColor(Color.BLACK)
    data.setValueTextSize(9f)
    return data
}




fun setUpRadarChart(rChart: RadarChart, date:DateTime, db: SampleDBOpenHelper, span: Int,context: Context) {


    val cnt = 5 //要素数
    var mTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

    val nutPartScore=calPartScore(date.year,date.month+1,date.day,span,db,context)?: TODO()
    val vals = ArrayList<RadarEntry>()

    var i=0



    //乱数データをリストに追加
    while(i<cnt) {
        var val1  = nutPartScore[i]
        vals.add(RadarEntry(val1))
        i=i+1
    }

    // Grid背景色
    rChart.setWebLineWidth(1f)
    rChart.setWebColor(Color.LTGRAY)
    rChart.setWebLineWidthInner(1f)
    rChart.setWebColorInner(Color.LTGRAY)
    rChart.setWebAlpha(100)
    rChart.setDescription(null)

    rChart.animateXY(1400, 1400, Easing.EaseInOutQuad);

    val xAxis = rChart.getXAxis()
    xAxis.typeface=mTypeface
    xAxis.setTextSize(9f)
    xAxis.setYOffset(0f)
    xAxis.setXOffset(0f)
    val labels = arrayOf("カロリー", "糖質", "タンパク質", "脂質","食物繊維")
    xAxis.setValueFormatter(IndexAxisValueFormatter(labels))
    xAxis.setTextColor(Color.BLACK)

    val yAxis = rChart.getYAxis()
    yAxis.typeface=mTypeface
    yAxis.setLabelCount(5, true)
    yAxis.setTextSize(9f)
    yAxis.setAxisMinimum(0f)
    yAxis.setAxisMaximum(100f)
    yAxis.setDrawLabels(true)

    val l = rChart.getLegend()
    l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
    l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
    l.setOrientation(Legend.LegendOrientation.HORIZONTAL)
    l.setDrawInside(false)
    l.typeface=mTypeface
    l.setXEntrySpace(7f)
    l.setYEntrySpace(5f)
    l.setTextColor(Color.WHITE)


    //データにラベルをつける
    val set1 = RadarDataSet(vals, "仮")

    set1.setColor(Color.rgb(103, 110, 129))
    set1.setFillColor(Color.rgb(103, 110, 129))
    set1.setDrawFilled(true)
    set1.setFillAlpha(180)
    set1.setLineWidth(2f)
    set1.setDrawHighlightCircleEnabled(true)
    set1.setDrawHighlightIndicators(false)

    //最終的に入れるデータ
    val sets = ArrayList<RadarDataSet>()
    sets.add(set1)
    setGraphData(rChart,sets)

}

fun setGraphData(rChart: RadarChart, set:List<RadarDataSet>) {
    val data = RadarData(set)
    var mTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    data.setValueTypeface(mTypeface)
    data.setValueTextSize(8f)
    data.setDrawValues(false)
    data.setValueTextColor(Color.BLACK)

    rChart.setData(data)
    //グラフを再描画する
    rChart.invalidate()
}

fun calPartScore(year:Int,month:Int,day:Int,span:Int,db: SampleDBOpenHelper,context: Context): Array<Float>?{
    var conditionSql = ""
    var selectionArgs = arrayOf("")
    val record = DBContract.Record
    val food = DBContract.Food
    var period=0
    val nut=NutritionHelper(context)

    // 日別の場合
    if (span == 0) {
        conditionSql += "${record.TABLE_NAME}.${record.YEAR} = ? AND " +
                "${record.TABLE_NAME}.${record.MONTH} = ? AND " +
                "${record.TABLE_NAME}.${record.DATE} = ?"
        selectionArgs = arrayOf(year.toString(), month.toString(), day.toString())
        period = 1
    }

    // 週別の場合
    else if (span == 1) {
        conditionSql += "${record.TABLE_NAME}.${record.YEAR} = ? " +
                "AND ${record.TABLE_NAME}.${record.MONTH} = ? " +
                "AND ${record.TABLE_NAME}.${record.DATE} >= ? " +
                "AND ${record.TABLE_NAME}.${record.DATE} <= ?"
        selectionArgs = arrayOf(year.toString(), month.toString())
        if (day < 8) {
            selectionArgs = arrayOf(year.toString(), month.toString(), "1", "7")
            period = 7
        } else if (day < 15) {
            selectionArgs = arrayOf(year.toString(), month.toString(), "8", "14")
            period = 7
        } else if (day < 22) {
            selectionArgs = arrayOf(year.toString(), month.toString(), "15", "21")
            period = 7
        } else if (day < 29) {
            selectionArgs = arrayOf(year.toString(), month.toString(), "22", "28")
            period = 7
        } else {
            selectionArgs = arrayOf(year.toString(), month.toString(), "29", "31")
            if (month == 2) {
                if (year % 4 == 0) {
                    period = 1
                } else {
                    period = 0
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                period = 2
            } else {
                period = 3
            }
        }
    }

    // 月別の場合
    else if (span == 2) {
        conditionSql += "${record.TABLE_NAME}.${record.YEAR} = ? " +
                "AND ${record.TABLE_NAME}.${record.MONTH} = ?"
        selectionArgs = arrayOf(year.toString(), month.toString())
        if (month == 2) {
            if (year % 4 == 0) {
                period = 29
            } else {
                period = 28
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            period = 30
        } else {
            period = 31
        }
    } else {
        println("invalid span")
        println("日別:span=0、週別:span=1、月別:span=2")
        return null
    }

    val result = db.searchRecord(
        record.TABLE_NAME, arrayOf("${food.TABLE_NAME}.${food.ID}"),
        condition = conditionSql, selectionArgs = selectionArgs,
        innerJoin = Join(food.TABLE_NAME, record.FOOD_ID, food.ID)
    ) ?: return TODO()

    val ids= mutableListOf<Int>()

    var i=0

    while(i<result.size)
    {
        ids.add(result[i].toInt())
        i++
    }
    val foodNutList = nut.getNutritions(ids)?:TODO()

    var calSum = 0f
    var sugarSum = 0f
    var fatSum = 0f
    var proteinSum = 0f
    var vitaminSum = 0f
    var mineralSum = 0f
    var fiberSum = 0f
    // 食事記録からこれまで摂取した栄養の合計を求める
    foodNutList.forEach {
        sugarSum += it.sugar
        fatSum += it.fat
        proteinSum += it.protein
        vitaminSum += it.vitamin
        mineralSum += it.mineral
        fiberSum += it.fiber
        calSum += it.calorie
    }

    val user = db.searchRecord(DBContract.UserInfo.TABLE_NAME)?: TODO()

    val age = user[4].toInt()
    val sex = user[5].toInt()
    val height = user[2].toFloat()
    val weight = user[3].toFloat()
    var cal = 0f
    var fiber = 20f
    var vitamin = 0f
    var mineral = 0f

    // Harris-Benedict式
    // 幼年時の計算は別途に必要と思われる
    if (sex == 0) {
        cal = 66f + (13.7f * weight) + (5.0f * height) - (6.8f * age)
        fiber = 20f
    } else if (sex == 1) {
        cal = 655f + (9.6f * weight) + (1.7f * height) - (4.7f * age)
        fiber = 18f
    }
    var protein = weight * 0.8f
    var fat = cal / 36f
    var sugar = cal * 0.15f

    val necNut=Nutrition("necessary", sugar*period, fat*period, protein*period,
        vitamin*period, mineral*period, fiber*period, cal*period)



    // それぞれの栄養分の過不足の割合
    var sugarPer = sugarSum/necNut.sugar
    if(sugarPer > 1) sugarPer = 1-(sugarPer-1) // 取りすぎ分を減点

    var fatPer = fatSum/necNut.fat
    if(fatPer > 1) fatPer = 1-(fatPer-1) // 取りすぎ分を減点

    var proteinPer = proteinSum/necNut.protein
    if(proteinPer > 1) proteinPer = 1-(proteinPer-1) // 取りすぎ分を減点

    var fiberPer = fiberSum/necNut.fiber
    if(fiberPer > 1) fiberPer = 1f // 上限以降は考えない

    var calPer = calSum/necNut.calorie
    if(calPer > 1) calPer = 1-(calPer-1) // 取りすぎ分を減点

    var scoreSugar=sugarPer*100+10
    var scoreFat=fatPer*100+10
    var scoreProtein=proteinPer*100+10
    var scoreFiber=100*fiberPer+10
    var scoreCal=100*calPer+10

    if(scoreSugar>100f){
        scoreSugar=100f
    }else if(scoreSugar<0){
        scoreSugar=0f
    }
    if(scoreFat>100){
        scoreFat=100f
    }else if(scoreFat<0){
        scoreFat=0f
    }
    if(scoreProtein>100f){
        scoreProtein=100f
    }else if(scoreProtein<0f){
        scoreProtein=0f
    }
    if(scoreFiber>100){
        scoreFiber=100f
    }else if(scoreFiber<0f){
        scoreFiber=0f
    }
    if(scoreCal>100f){
        scoreCal=100f
    }else if(scoreCal<0f){
        scoreCal=0f
    }


    val resultScore = arrayOf(calPer,sugarPer ,proteinPer,fatPer,fiberPer)



    return resultScore
}


class DateTime() {
    private var mCalendar: Calendar = Calendar.getInstance().also { it.clear() }
    //DateTime ver 1.0
    //プロパティ 日付など各値を取得できる
    var year: Int = mCalendar.get(Calendar.YEAR)
        get() = mCalendar.get(Calendar.YEAR)
    var month: Int = mCalendar.get(Calendar.MONTH)
        get() = mCalendar.get(Calendar.MONTH)
    var day: Int = mCalendar.get(Calendar.DAY_OF_MONTH)
        get() = mCalendar.get(Calendar.DAY_OF_MONTH)
    var hour: Int = mCalendar.get(Calendar.HOUR_OF_DAY)
        get() = mCalendar.get(Calendar.HOUR_OF_DAY)
    var minute: Int = mCalendar.get(Calendar.MINUTE)
        get() = mCalendar.get(Calendar.MINUTE)
    var second: Int = mCalendar.get(Calendar.SECOND)
        get() = mCalendar.get(Calendar.SECOND)
    var isAm: Boolean = mCalendar.get(Calendar.AM_PM) == Calendar.AM
        get() = mCalendar.get(Calendar.AM_PM) == Calendar.AM
    var dayOfWeek: Int = mCalendar.get(Calendar.DAY_OF_WEEK)
        get() = mCalendar.get(Calendar.DAY_OF_WEEK)
    var dayOfYear: Int = mCalendar.get(Calendar.DAY_OF_YEAR)
        get() = mCalendar.get(Calendar.DAY_OF_YEAR)

    //日付計算用
    fun add(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DateTime {
        mCalendar.apply {
            add(Calendar.YEAR, year)
            add(Calendar.MONTH, month)
            add(Calendar.DAY_OF_MONTH, month)
            add(Calendar.HOUR, hour)
            add(Calendar.MINUTE, minute)
            add(Calendar.MILLISECOND, second)
        }
        return this
    }
    fun addYeats(year: Int): DateTime = this.also { it.mCalendar.add(Calendar.YEAR, year) }
    fun addMonths(month: Int): DateTime = this.also { it.mCalendar.add(Calendar.MONTH, month) }
    fun addDays(day: Int): DateTime = this.also { it.mCalendar.add(Calendar.DAY_OF_MONTH, day) }
    fun addHours(hour: Int): DateTime = this.also { it.mCalendar.add(Calendar.HOUR, hour) }
    fun addMinutes(minute: Int): DateTime = this.also { it.mCalendar.add(Calendar.MINUTE, minute) }
    fun addSeconds(second: Int): DateTime = this.also { it.mCalendar.add(Calendar.SECOND, second) }

    //日付を設定するメソッド
    fun setDateTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) {
        mCalendar.set(year, month, day, hour, minute, second)
    }
    fun setDateTime(date: Date) {
        mCalendar.time = date
        mCalendar.set(Calendar.MILLISECOND, 0)
    }
    fun setDateTime(calendar: Calendar) {
        setDateTime(calendar.time)
    }

    //日付を文字列に変換するるメソッド
    override fun toString(): String = DateFormat.format("yyyy/MM/dd kk/mm/ss", mCalendar.time).toString()
    fun toString(format: String): String = DateFormat.format(format, mCalendar.time).toString()

    //その他メソッド　日付のクリア
    fun clear(): Unit = mCalendar.clear()
    //Date()の取得
    fun toDate(): Date = mCalendar.time
    //DateTime同士の比較 ==が使える
    override operator fun equals(dateTime: Any?): Boolean {
        return if (dateTime is DateTime) {
            compareTo(dateTime as DateTime) == 0
        } else {
            false
        }
    }
    //DateTime同士の比較 >=が使える
    operator fun compareTo(dateTime: DateTime): Int {
        val calendar: Calendar = Calendar.getInstance().also { it.time = dateTime.toDate() }
        return mCalendar.compareTo(calendar)
    }

    companion object {
        //現在の日時を所有したDateTimeオブジェクトを返す
        fun now(): DateTime {
            return DateTime().also {
                it.mCalendar = Calendar.getInstance()
                it.mCalendar.set(Calendar.MILLISECOND, 0)
            }
        }
        //文字列から日付を作成しDateTimeオブジェクトを返す
        fun parse(dateTimeString: String): DateTime {
            val dateTime = DateTime()
            try {
                dateTime.mCalendar.time = SimpleDateFormat("yyyy/MM/dd kk:mm:ss", Locale.JAPAN).also { it.isLenient = false }.parse(dateTimeString)
            } catch (e: Exception) {
                throw IllegalArgumentException("can't convert string to DateTime")
            }
            return dateTime
        }
        fun parse(dateTimeString: String, format: String): DateTime {
            val dateTime = DateTime()
            try {
                dateTime.mCalendar.time = SimpleDateFormat(format, Locale.JAPAN).also { it.isLenient = false }.parse(dateTimeString)
            } catch (e: Exception) {
                throw IllegalArgumentException("can't convert string to DateTime")
            }
            return dateTime
        }
        //与えられた文字列を変換できるかどうか
        fun tryParse(dateTimeString: String): Boolean {
            return try {
                SimpleDateFormat("yyyy/MM/dd kk:mm:ss", Locale.JAPAN).also { it.isLenient = false }.parse(dateTimeString)
                true
            } catch (e: Exception) {
                false
            }
        }
        fun tryParse(dateTimeString: String, format: String): Boolean {
            return try {
                SimpleDateFormat(format, Locale.JAPAN).also { it.isLenient = false }.parse(dateTimeString)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
package com.example.wse2019


import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry


class TopFragment() : Fragment() {

    val foodManager = FoodManager()
    val cm = CalendarManager()
    val today = cm.calendar.time
    private lateinit var recommendFood: RecommendFood

    companion object {
        fun newInstance(): TopFragment {
            val fragment = TopFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recommendFood.init()
    }

    @TargetApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_top, container, false)

        val cm = CalendarManager()
        val nh = NutritionHelper(context)

        val today = cm.calendar.time

        // --------------------------------------------------
        //  今週の得点
        // --------------------------------------------------
        val score: Float = nh.recordScore(
            cm.getYear(today)   .toInt(),
            cm.getMonth(today)  .toInt(),
            cm.getDate(today)   .toInt(),
            1) ?: throw NullPointerException()

        val scoreChart: PieChart = view.findViewById(R.id.ft_scorePieChart)
        when (context) {
            null -> throw NullPointerException()
            else -> createScorePieChart(context!!, scoreChart, score)
        }

        // --------------------------------------------------
        //  最もおすすめな品目
        // --------------------------------------------------
        val mostRecommendFood = view.findViewById<LinearLayout>(R.id.ft_recommendFood)
        val image       = view.findViewById<ImageView>(R.id.ft_recommendImageView)
        val name        = view.findViewById<TextView>(R.id.ft_foodNameTextView)
        val favorite    = view.findViewById<ImageView>(R.id.ft_favoriteImageView)
        val nutrition   = view.findViewById<TextView>(R.id.ft_recommendFoodNutritionTextView)
        mostRecommendFood.apply {
            setOnClickListener {
                showFoodInformationDialog(recommendFood.first.id, context, container)
            }
        }
        image.apply {
            setImageBitmap(foodManager.getBitmap(context, recommendFood.first.id))
        }
        name.apply {
            text = recommendFood.first.name
        }
        favorite.apply {
            visibility = when (recommendFood.first.favorite) {
                1 -> View.VISIBLE
                0 -> View.GONE
                else -> throw AssertionError()
            }
        }
        nutrition.apply {
            text = "カロリー:\n %.0f kcal\n糖質:\n %.1f g\n脂質:\n %.1f g\nたんぱく質:\n %.1f g\n食物繊維:\n %.1f g".format(
                recommendFood.first.nutrition.calorie,
                recommendFood.first.nutrition.sugar,
                recommendFood.first.nutrition.fat,
                recommendFood.first.nutrition.protein,
                recommendFood.first.nutrition.fiber
            )
        }

        // --------------------------------------------------
        //  そのほかのおすすめ品目
        // --------------------------------------------------
        val otherRecommendFood: ListView = view.findViewById(R.id.ft_otherRecommendFoodListView)
        otherRecommendFood.apply {
            adapter = recommendFood.getAdapter()
            setOnItemClickListener { view, parent, position, id ->
                val food = recommendFood.getItem(position)
                showFoodInformationDialog(food.id, context, container)
            }
        }
        recommendFood.updateHeight(otherRecommendFood)

        // --------------------------------------------------
        //  もっと見る！ボタン
        // --------------------------------------------------
        val showMore: LinearLayout = view.findViewById(R.id.ft_showMoreTextView)
        showMore.apply {
            setOnClickListener {
                recommendFood.showMore(otherRecommendFood)
                if (recommendFood.isAllShowed()) {
                    showMore.visibility = View.GONE
                }
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        recommendFood = when (context) {
            null -> throw NullPointerException()
            else -> RecommendFood()
        }
    }
    override fun onDetach() {
        super.onDetach()
    }

    // ==================================================
    // ==================================================
    //
    //  RecommendFood
    //  - おすすめ品目を管理するクラス
    //
    // ==================================================
    // ==================================================
    private inner class RecommendFood {
        private val adapter: FoodListAdapter = when (context) {
            null -> throw NullPointerException()
            else -> FoodListAdapter(context!!)
        }
        val first: FoodListAdapter.Food
            get() = adapter.allFoods[0]
        val count: Int
            get() = adapter.count
        fun getAdapter() : FoodListAdapter { return adapter }
        fun getItem(position: Int) : FoodListAdapter.Food { return adapter.getItem(position) }

        // その他のおすすめ品目としてリストビューに表示する品目に関する定数
        val OTHER_RECOMMEND_FROM_INDEX          = 1     // 最初の品目のインデックス
        val OTHER_RECOMMEND_INITIAL_SHOW_NUM    = 5     // 表示品目数の初期値
        val OTHER_RECOMMEND_SHOW_MORE_NUM       = 10    // 「もっと見る！」ボタンが押される毎に増加する品目表示数

        fun init() {
            adapter.allFoods.addAll(search(cm.getYear(today).toInt(), cm.getMonth(today).toInt(), cm.getDate(today).toInt(), 1))
            setOtherRecommendFoodsToShow(OTHER_RECOMMEND_FROM_INDEX, OTHER_RECOMMEND_INITIAL_SHOW_NUM)
        }

        fun showMore(listView: ListView) {
            val currentShowNum = adapter.foodsToShow.size
            setOtherRecommendFoodsToShow(OTHER_RECOMMEND_FROM_INDEX, currentShowNum+OTHER_RECOMMEND_SHOW_MORE_NUM+1)
            updateHeight(listView)
            applyChanges()
        }

        fun isAllShowed(): Boolean {
            return adapter.foodsToShow == adapter.allFoods.subList(OTHER_RECOMMEND_FROM_INDEX, adapter.allFoods.size)
        }

        // --------------------------------------------------
        //  updateHeight
        //  - リストビューの高さを更新
        // --------------------------------------------------
        fun updateHeight(listView: ListView) {

            var totalHeight: Int = 0

            // 各行の高さを合計していく
            for (position in 0..adapter.count.minus(1)) {
                val item: View = adapter.getView(position, null, listView)
                item.measure(0, 0)
                totalHeight += item.measuredHeight
            }

            // 区切り線の高さを加える
            totalHeight += listView.dividerHeight * listView.adapter.count - 1

            // 更新
            listView.layoutParams = listView.layoutParams.apply { height = totalHeight }

        }
        // --------------------------------------------------

        private fun setOtherRecommendFoodsToShow(
            fromIndex   : Int,
            showNum     : Int
        ) {
            var toIndex = fromIndex + showNum

            // 値のチェック
            if (toIndex > adapter.allFoods.size) {
                toIndex = adapter.allFoods.size
            }

            // その他のおすすめ品目リストを更新
            adapter.foodsToShow = adapter.allFoods.subList(fromIndex, toIndex)
        }

        private fun search(year: Int, month: Int, date: Int, span: Int) : List<FoodListAdapter.Food> {
            val foods: MutableList<FoodListAdapter.Food> = mutableListOf()

            val db = SampleDBOpenHelper(context)
            val nh = NutritionHelper(context)
            val foodT = DBContract.Food // Food table

            // ランク付けされたおすすめ品目を取得
            val recommend = nh.selectFood(year, month, date, span) ?: throw NullPointerException()

            // 取得した品目の情報を取得
            recommend.foodIDList.forEach { foodId ->
                val result: List<String> = db.searchRecord(
                    tableName   = foodT.TABLE_NAME,
                    column      = arrayOf(foodT.NAME, foodT.FAVORITE),
                    condition   = "${foodT.ID} = ${foodId}"
                ) ?: throw NullPointerException()
                foods.add(FoodListAdapter.Food(
                    id          = foodId,
                    name        = result[0],
                    favorite    = result[1].toInt()))
            }

            // 各品目の栄養を調べて格納
            foods.forEach { food ->
                food.nutrition = nh.getNutritions(listOf(food.id))?.first() ?: throw NullPointerException()
            }

            return foods
        }

        fun applyChanges() {
            adapter.notifyDataSetChanged()
        }

    }

    // --------------------------------------------------
    //  showFoodInformationDialog - 品目の情報を表示する
    // --------------------------------------------------
    fun showFoodInformationDialog(foodId: Int, context: Context, container: ViewGroup?) {
        AlertDialog.Builder(context).apply {
            setView(foodManager.getFoodInformationView(foodId, context, container))
        }.show()
    }



    // --------------------------------------------------
    //  createScorePieChart - スコア円グラフをつくる
    // --------------------------------------------------
    @SuppressLint("ResourceType")
    private fun createScorePieChart(context: Context, pieChart: PieChart, score: Float) {
        pieChart.apply {
            holeRadius = 75f
            transparentCircleRadius = holeRadius+10f
            isRotationEnabled = false
            description = null
            legend.isEnabled = false
            centerText = "%.0f".format(score)
            setCenterTextSize(50f)
            setCenterTextColor(Color.parseColor(getString(R.color.colorAccent)))
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            data = createScorePieData(score)
            invalidate()
            animateXY(3000, 3000)
        }
    }
    @SuppressLint("ResourceType")
    private fun createScorePieData(score: Float): PieData {
        val values: MutableList<PieEntry>   = mutableListOf()
        val colors: MutableList<Int>        = mutableListOf()

        values += PieEntry(score)
        values += PieEntry(100f-score)

        colors += Color.YELLOW
        colors += Color.argb(0, 255, 255, 255)

        val dataSet = PieDataSet(values, null)
        dataSet.apply {
            sliceSpace = 5f
            setDrawValues(false)
            setColors(colors)
        }

        return PieData(dataSet)
    }



    // --------------------------------------------------
    //  replaceFragment - フラグメントを切り替える
    // --------------------------------------------------
    fun replaceFragment(
        f: Fragment
    ) {
        val listener = context as TabFragment.OnRegisterNewCondateSelectedListener
        listener.replaceFragment(f)
    }
}
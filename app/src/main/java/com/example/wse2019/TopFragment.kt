package com.example.wse2019


import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper


class TopFragment() : Fragment() {

    val foodManager = FoodManager()
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

        // --------------------------------------------------
        //  今週の得点
        // --------------------------------------------------
        val score = view.findViewById<TextView>(R.id.ft_scoreTextView)
        score.apply {
            //text = /* score */
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
            text = "カロリー:\n %.0f kcal\n糖質:\n %.1f g\n脂質:\n %.1f g\nたんぱく質:\n %.1f g\nミネラル:\n %.1f g\nビタミン:\n %.1f g\n食物繊維:\n %.1f g".format(
                recommendFood.first.nutrition.calorie,
                recommendFood.first.nutrition.sugar,
                recommendFood.first.nutrition.fat,
                recommendFood.first.nutrition.protein,
                recommendFood.first.nutrition.mineral,
                recommendFood.first.nutrition.vitamin,
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
        private val adapter: RecommendFoodAdapter = when (context) {
            null -> throw NullPointerException()
            else -> RecommendFoodAdapter(context!!)
        }
        val first: RecommendFoodAdapter.Food
            get() = adapter.allFoods[0]
        val count: Int
            get() = adapter.count
        fun getAdapter() : RecommendFoodAdapter { return adapter }
        fun getItem(position: Int) : RecommendFoodAdapter.Food { return adapter.getItem(position) }

        // その他のおすすめ品目としてリストビューに表示する品目に関する定数
        val OTHER_RECOMMEND_FROM_INDEX          = 1     // 最初の品目のインデックス
        val OTHER_RECOMMEND_INITIAL_SHOW_NUM    = 5     // 表示品目数の初期値
        val OTHER_RECOMMEND_SHOW_MORE_NUM       = 10    // 「もっと見る！」ボタンが押される毎に増加する品目表示数

        fun init() {
            adapter.allFoods.addAll(search())
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

        private fun search() : List<RecommendFoodAdapter.Food> {
            val foods: MutableList<RecommendFoodAdapter.Food> = mutableListOf()

            // --------------------------------------------------
            //  仮
            // --------------------------------------------------
            val db = SampleDBOpenHelper(context)
            val foodT = DBContract.Food // Food table

            val result: List<String> = db.searchRecord(
                tableName = foodT.TABLE_NAME,
                column = arrayOf(
                    foodT.ID,
                    foodT.NAME,
                    foodT.FAVORITE)
            ) ?: throw NullPointerException("searchRecord was failed")

            var i = 0
            while (i < result.size) {
                foods.add(RecommendFoodAdapter.Food(
                    id      = result[i++].toInt(),
                    name    = result[i++],
                    favorite = result[i++].toInt()
                ))
            }
            // --------------------------------------------------

            // 各品目の栄養を調べて格納
            val nh = NutritionHelper(context)
            foods.forEach { food ->
                food.nutrition = nh.getNutrition(food.name) ?: throw NullPointerException()
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
            show()
        }
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
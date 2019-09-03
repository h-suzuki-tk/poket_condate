package com.example.wse2019

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper

class FoodSearchResultAdapter(context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val context = context

    // ----- [表示用]品目データクラス -----
    data class Food(
        val id      : Int,
        val iconID  : Int = R.drawable.ic_menu_gallery,
        val name    : String,
        val category: Int,
        var favorite: Int
    )

    val favorite_resourceID= listOf<Int>(android.R.drawable.btn_star_big_off, android.R.drawable.btn_star_big_on)
    var foods = mutableListOf<Food>()   // [表示用]品目プロパティ

    override fun getCount(): Int {
        return foods.size
    }

    override fun getItem(position: Int): Food {
        return foods[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View             = this.inflater.inflate(R.layout.food_search_result_row, parent, false)
        val parent: ListView    = parent as ListView

        // アイコンのセット
        val icon: ImageView = v.findViewById(R.id.foodImageView)
        icon.setImageResource(foods[position].iconID)

        // 品目名のセット
        val name: TextView = v.findViewById(R.id.foodTextView)
        name.text = foods[position].name

        // お気に入り済みか否かのセット
        val favorite: ImageButton = v.findViewById(R.id.favoriteButton)
        favorite.setImageResource(favorite_resourceID[foods[position].favorite])
        favorite.setOnClickListener {
            parent.performItemClick(v, position, R.id.favoriteButton.toLong())
        }

        // 追加ボタンのセット
        val add: Button = v.findViewById(R.id.addButton)
        add.setOnClickListener { parent.performItemClick(v, position, R.id.addButton.toLong()) }

        return v
    }

    // ------------------------------------------------------------
    //  お気に入り情報を切り替え
    // ------------------------------------------------------------
    fun switchFavorite(position: Int) {
        val current: Int = foods[position].favorite

        // ----- [表示用]プロパティの修正 ------
        foods[position].favorite = when (current) {
            0       -> 1
            1       -> 0
            else    -> throw AssertionError()
        }

        // ----- データベースの修正 -----
        val db = SampleDBOpenHelper(context).writableDatabase
        val foodTable = DBContract.Food

        val values = ContentValues()
        values.put("favorite", foods[position].favorite)
        db.update(
            "foods",
            values,
            "id = ${foods[position].id}",
            null)

        // うまくいかない
        /*
        db.updateRecord(
            tablename       = foodTable.TABLE_NAME,
            column          = arrayOf(
                foodTable.FAVORITE),
            convert         = arrayOf(
                "${foods[position].favorite}"),
            condition       = "${foodTable.ID} -> ?",
            selectionArgs   = arrayOf(
                "${foods[position].id}")
        )
        */
    }

    // ------------------------------------------------------------
    //  searchFoods - 食べ物を検索 -
    // ------------------------------------------------------------
    fun searchFoods(
        name       : String?    = null,
        favorite   : Int        = 0,
        category   : Int        = 0
    ) {
        val db = SampleDBOpenHelper(context)
        val foodTable = DBContract.Food
        var condition = ""

        // ----- 条件の設定 -----
        if (name != null && name.isNotBlank() && name.isNotEmpty()) {
            condition += "${foodTable.NAME} like '%${name}%'"
        }
        if (favorite != 0) {
            if (condition.isNotEmpty()) { condition += " and " }
            condition += "${foodTable.FAVORITE} = ${favorite}"
        }
        if (category != 0) {
            if (condition.isNotEmpty()) { condition += " and " }
            condition += "${foodTable.CATEGORY} = ${category}"
        }

        // ----- 検索 -----
        val result: List<String> = db.searchRecord(
            tableName   = foodTable.TABLE_NAME,
            column      = arrayOf(
                foodTable.ID,
                foodTable.NAME,
                foodTable.CATEGORY,
                foodTable.FAVORITE),
            condition   = when (condition.isEmpty()) {
                true    -> null
                false   -> condition }
        ) ?: throw NullPointerException("searchRecord was failed.")

        // ----- 格納 -----
        var i = 0
        while (i < result.size) {
            this.foods.add(Food(
                id          = result[i++].toInt(),
                name        = result[i++],
                category    = result[i++].toInt(),
                favorite    = result[i++].toInt()
                ))
        }
    }

    fun clear() {
        this.foods.clear()
    }

}
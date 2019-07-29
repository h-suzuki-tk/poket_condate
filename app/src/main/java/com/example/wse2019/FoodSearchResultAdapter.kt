package com.example.wse2019

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class FoodSearchResultAdapter(context: Context) : BaseAdapter() {
    val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    data class Food(
        val id: Int,
        val iconID: Int,
        val name: String,
        var favorite: Int
    )

    val favorite_resourceID= listOf<Int>(android.R.drawable.btn_star_big_off, android.R.drawable.btn_star_big_on)
    var foods = mutableListOf<Food>(
        Food(1, R.drawable.ic_menu_gallery, "いくらごはん", 0),
        Food(2, R.drawable.ic_menu_manage, "うなぎごはん", 1),
        Food(3, R.drawable.ic_menu_camera, "肉ごはん", 0)) // 初期化

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
            favorite.setImageResource(favorite_resourceID[foods[position].favorite])}

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

        // ----- 表示用プロパティの修正 ------
        foods[position].favorite = when (current) {
            0       -> 1
            1       -> 0
            else    -> throw AssertionError()
        }

        // ----- データベースの修正 -----

        /***** ここに記述 *****/

    }

}
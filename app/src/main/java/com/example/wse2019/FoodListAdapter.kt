package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_edit_inf.view.*
import java.io.IOException
import java.io.InputStream
import java.lang.AssertionError

const val FOOD_IMAGE_DIRECTORY_NAME = "foods"

class FoodListAdapter(val context: Context) : BaseAdapter() {

    // --------------------------------------------------
    //  品目情報
    // --------------------------------------------------
    data class Food(
        var id: Int = 0,
        var name: String = "",
        var favorite: Int = 0,
        var nutrition: Nutrition = Nutrition("", 0f, 0f, 0f, 0f, 0f, 0f, 0f)
    )
    var allFoods: MutableList<Food> = mutableListOf()
    var foodsToShow: MutableList<Food> = mutableListOf()
    // --------------------------------------------------

    // 表示のための情報
    data class ViewHolderItem(
        val layout      : LinearLayout,
        val image       : ImageView,
        val name        : TextView,
        val favorite    : ImageView,
        val nutrition   : TextView
    )

    override fun getCount(): Int {
        return foodsToShow.size
    }

    override fun getItem(position: Int): Food {
        return foodsToShow[position]
    }

    override fun getItemId(position: Int): Long {
        return foodsToShow[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // ビューホルダーの設定
        val (viewHolder, view) = when (convertView) {
            null -> {
                val view = LayoutInflater.from(context).inflate(R.layout.food_row, parent, false)
                val viewHolder = ViewHolderItem(
                    layout      = view.findViewById(R.id.fr_layout),
                    image       = view.findViewById(R.id.fr_imageView),
                    name        = view.findViewById(R.id.fr_foodNameTextView),
                    favorite    = view.findViewById(R.id.fr_favoriteImageView),
                    nutrition   = view.findViewById(R.id.fr_foodNutritionTextView)
                )
                view.tag = viewHolder
                viewHolder to view
            }
            else -> convertView.tag as ViewHolderItem to convertView
        }

        // 各ビューの設定
        viewHolder.layout.apply {
            setOnClickListener { (parent as ListView).performItemClick(view, position, R.id.fr_layout.toLong()) }
        }
        val foodManager = FoodManager()
        viewHolder.image.apply {
            setImageBitmap(foodManager.getBitmap(context, foodsToShow[position].id))
        }
        viewHolder.name.apply {
            text = foodsToShow[position].name
        }
        viewHolder.favorite.apply {
            visibility = when (foodsToShow[position].favorite) {
                1 -> View.VISIBLE
                0 -> View.GONE
                else -> throw AssertionError()
            }
        }
        viewHolder.nutrition.apply {
            text = "カロリー: %.0f kcal / 糖質: %.1f g / 脂質: %.1f g / たんぱく質: %.1f g / 食物繊維: %.1f g".format(
                foodsToShow[position].nutrition.calorie,
                foodsToShow[position].nutrition.sugar,
                foodsToShow[position].nutrition.fat,
                foodsToShow[position].nutrition.protein,
                foodsToShow[position].nutrition.fiber
            )
        }

        return view
    }


}
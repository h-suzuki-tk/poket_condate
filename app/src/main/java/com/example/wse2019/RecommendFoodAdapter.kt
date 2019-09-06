package com.example.wse2019

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

const val FOOD_IMAGE_DIRECTORY_NAME = "foods"

class RecommendFoodAdapter(val context: Context) : BaseAdapter() {

    // --------------------------------------------------
    //  品目情報
    // --------------------------------------------------
    data class Food(
        val id: Int,
        val name: String
    )
    var allFoods: MutableList<Food> = mutableListOf()
    var foodsToShow: MutableList<Food> = mutableListOf()
    // --------------------------------------------------

    // 表示のための情報
    data class ViewHolderItem(
        val layout  : LinearLayout,
        val image   : ImageView,
        val name    : TextView
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
                val view = LayoutInflater.from(context).inflate(R.layout.recommend_food_row, parent, false)
                val viewHolder = ViewHolderItem(
                    layout  = view.findViewById(R.id.rfr_layout),
                    image   = view.findViewById(R.id.rfr_imageView),
                    name    = view.findViewById(R.id.rfr_foodNameTextView)
                )
                view.tag = viewHolder
                viewHolder to view
            }
            else -> convertView.tag as ViewHolderItem to convertView
        }

        // 各ビューの設定
        viewHolder.layout.apply {
            setOnClickListener { (parent as ListView).performItemClick(view, position, R.id.rfr_layout.toLong()) }
        }
        val foodManager = FoodManager()
        viewHolder.image.apply {
            setImageBitmap(foodManager.getBitmap(context, foodsToShow[position].id))
        }
        viewHolder.name.apply {
            text = foodsToShow[position].name
        }

        return view
    }


}
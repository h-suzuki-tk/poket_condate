package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class IngredientSearchResultAdapter(val context: Context) : BaseAdapter() {

    // 材料情報
    data class Ingredient(
        val id: Int,
        val name: String,
        val unit: String
    )
    var ingredients: MutableList<Ingredient> = mutableListOf()

    // 各行で表示する情報
    data class ViewHolderItem(
        var name        : TextView,
        val addButton   : Button
    )

    override fun getCount(): Int {
        return ingredients.size
    }

    override fun getItem(position: Int): Ingredient {
        return ingredients[position]
    }

    override fun getItemId(position: Int): Long {
        return ingredients[position].id.toLong()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val parent = parent as ListView

        // ビューホルダーの設定
        val (viewHolder, view) = when (convertView) {
            null -> {
                val view = LayoutInflater.from(context).inflate(R.layout.ingredients_search_result_row, parent, false)
                val viewHolder = ViewHolderItem(
                    name        = view.findViewById(R.id.isrr_name),
                    addButton   = view.findViewById(R.id.isrr_addButton)
                )
                view.tag = viewHolder
                viewHolder to view
            }
            else -> convertView.tag as ViewHolderItem to convertView
        }

        // 各ビューの設定
        viewHolder.name.apply {
            text = ingredients[position].name
        }
        viewHolder.addButton.apply {
            setOnClickListener { parent.performItemClick(view, position, R.id.isrr_addButton.toLong()) }
        }

        return view
    }
}
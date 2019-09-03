package com.example.wse2019

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class IngredientSearchResultAdapter(val context: Context) : BaseAdapter() {

    // 材料情報
    data class Ingredient(
        val id: Int,
        val name: String,
        var number: Float = Float.NaN,
        val unit: String
    )
    var ingredients: MutableList<Ingredient> = mutableListOf()

    // 各行で表示する情報
    data class ViewHolderItem(
        var name        : TextView,
        var number      : EditText,
        val unit        : TextView,
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
                    number      = view.findViewById(R.id.isrr_number),
                    unit        = view.findViewById(R.id.isrr_unit),
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
        viewHolder.number.apply {
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    val str = text.toString()
                    ingredients[position].number = when (str) {
                        ""      -> Float.NaN
                        else    -> str.toFloat()
                    }
                }
                override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) { /* なにもしない */ }
                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int)    { /* なにもしない */ }
            })
        }
        viewHolder.unit.apply {
            text = ingredients[position].unit
        }
        viewHolder.addButton.apply {
            setOnClickListener { parent.performItemClick(view, position, R.id.isrr_addButton.toLong()) }
        }

        return view
    }
}
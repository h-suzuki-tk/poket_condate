package com.example.wse2019

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.media.Image
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import java.lang.String.format

class TempRegistrationStateAdapter(context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val context = context

    // ----- 仮登録状況で扱うデータ -----
    data class Food(
        val id      : Int,
        val name    : String,
        val number  : Float
    )
    private val foods: MutableList<Food> = mutableListOf()

    override fun getCount(): Int {
        return foods.size
    }

    override fun getItem(position: Int): Food {
        return foods[position]
    }

    override fun getItemId(position: Int): Long {
        return foods[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View             = this.inflater.inflate(R.layout.temp_registration_state_row, parent, false)
        val parent: ListView    = parent as ListView

        val foodName: TextView          = v.findViewById(R.id.foodNameTextView)
        foodName.text = String.format("%s[%.1f人前]",
            foods[position].name,
            foods[position].number
        )

        val removeButton: ImageButton   = v.findViewById(R.id.removeImageButton)
        removeButton.setOnClickListener { parent.performItemClick(v, position, R.id.removeImageButton.toLong()) }

        return v
    }

    fun add(food: Food) {
        foods.add(food)
    }

    fun remove(position: Int) {
        foods.removeAt(position)
    }

    fun clear() {
        foods.clear()
    }

}
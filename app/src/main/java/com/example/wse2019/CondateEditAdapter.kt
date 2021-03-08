package com.example.wse2019

import android.content.Context
import android.database.sqlite.SQLiteException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Join
import com.example.sample.Table

class CondateEditAdapter(context: Context) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val context = context
    private val m_foods        : MutableList<Food>
    private val m_oldRecordIds : MutableList<Int>

    init {
        m_foods        = mutableListOf<Food>()
        m_oldRecordIds = mutableListOf<Int>()
    }

    data class Food(
        val id     : Int,
        val name   : String,
        val number : Float
    )

    override fun isEmpty() : Boolean {
        return m_foods.size == 0
    }

    override fun getCount(): Int {
        return m_foods.size
    }

    override fun getItem(position: Int): Food {
        return m_foods[position]
    }

    override fun getItemId(position: Int): Long {
        return m_foods[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View          = this.inflater.inflate(R.layout.condate_edit_row, parent, false)
        val parent: ListView = parent as ListView

        val foodName: TextView          = v.findViewById(R.id.foodNameTextView)
        foodName.text = String.format("%s[%.1f人前]",
            m_foods[position].name,
            m_foods[position].number
        )

        val removeButton: ImageButton   = v.findViewById(R.id.removeImageButton)
        removeButton.setOnClickListener { parent.performItemClick(v, position, R.id.removeImageButton.toLong()) }

        return v
    }

    fun reset(day: DayManager) {

        // データベース検索のための宣言・初期化
        val db      = SampleDBOpenHelper(context)
        val recordT = DBContract.Record // Record table
        val foodT   = DBContract.Food   // Food table

        // 検索
        val result: List<String> = db.searchRecord(
            tableName = foodT.TABLE_NAME,
            column = arrayOf(
                "${recordT.TABLE_NAME}.${recordT.ID}",
                "${foodT.TABLE_NAME}.${foodT.ID}",
                foodT.NAME,
                recordT.NUMBER
            ),
            condition = "${recordT.TABLE_NAME}.${recordT.YEAR}  = ${day.year}" +
                    " and " + "${recordT.TABLE_NAME}.${recordT.MONTH} = ${day.month}" +
                    " and " + "${recordT.TABLE_NAME}.${recordT.DATE}  = ${day.date}" +
                    " and " + "${recordT.TABLE_NAME}.${recordT.TIME}  = ${day.time}",
            innerJoin = Join(
                tablename = recordT.TABLE_NAME,
                column1 = foodT.ID,
                column2 = recordT.FOOD_ID
            )
        ) ?: throw NullPointerException("searchRecord was failed.")

        // 検索結果を格納
        m_oldRecordIds.clear()
        this.clear()
        var i = 0
        while (i < result.size) {
            m_oldRecordIds.add(result[i++].toInt())
            this.add(
                Food(
                    id     = result[i++].toInt(),
                    name   = result[i++],
                    number = result[i++].toFloat()
                )
            )
        }
        this.notifyDataSetChanged()
    }

    fun update(day: DayManager) {

        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }

        // 古い記録を削除
        val rcd = DBContract.Record
        for (id in m_oldRecordIds) {
            if (!db.deleteRecord(
                tablename     = rcd.TABLE_NAME,
                condition     = "${rcd.ID} = ${id}"
            )) { throw SQLiteException("deleteRecord was failed.") }
        }
        m_oldRecordIds.clear()

        // 新しい献立で記録を更新
        for (food in m_foods) {
            db.insertRecord(
                Table.Record(
                    food_id = food.id,
                    year    = day.year,
                    month   = day.month,
                    date    = day.date,
                    time    = day.time,
                    number  = food.number
                )
            )
        }
        this.reset(day)
        this.notifyDataSetChanged()
    }

    fun getIds() : List<Int> {
        val ids = mutableListOf<Int>()
        for (r in m_foods) { ids.add(r.id) }
        return ids
    }

    fun add(food: Food) {
        m_foods.add(food)
    }

    fun addAll(foods: MutableList<Food>) {
        m_foods.addAll(foods)
    }

    fun remove(position: Int) {
        m_foods.removeAt(position)
    }

    fun clear() {
        m_foods.clear()
    }

}

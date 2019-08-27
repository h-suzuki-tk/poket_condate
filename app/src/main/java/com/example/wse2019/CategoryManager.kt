package com.example.wse2019

import android.content.Context
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper

class CategoryManager {

    fun getCategories(context: Context) : List<String> {
        val db = SampleDBOpenHelper(context)
        val categoryTable = DBContract.Category
        return db.searchRecord(
            tableName   = categoryTable.TABLE_NAME,
            column      = arrayOf(
                categoryTable.NAME
            )
        ) ?: throw NullPointerException("searchRecord was failed")
    }

}
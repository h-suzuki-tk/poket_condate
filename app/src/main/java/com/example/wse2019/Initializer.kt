package com.example.sample

import android.content.Context
import android.util.Log
import com.example.wse2019.csvImportHelper

//db作り直しからデフォルト値入れるまで
fun initializer(context: Context) {
    Log.d("program check", "initialize start")
    val db = SampleDBOpenHelper(context).writableDatabase
    val DB = SampleDBOpenHelper(context)

    DB.dropTables(db)
    DB.onCreate(db)
    db.close()

    default.forEach {
        DB.insertRecord(it)
    }
    csvImportHelper(context,DB,"Ingredients/014.csv")
    csvImportHelper(context,DB,"Ingredients/011.csv")
    csvImportHelper(context,DB,"Ingredients/015.csv")
    csvImportHelper(context,DB,"Ingredients/016.csv")
    csvImportHelper(context,DB,"Ingredients/017.csv")
    csvImportHelper(context,DB,"Ingredients/018.csv")

}
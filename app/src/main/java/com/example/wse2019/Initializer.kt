package com.example.sample

import android.content.Context
import android.util.Log

//db作り直しからデフォルト値入れるまで
fun initializer(context: Context) {
    Log.d("program check", "initialize start")
    val db = SampleDBOpenHelper(context).writableDatabase
    val DB = SampleDBOpenHelper(context)

    DB.dropTables(db)
    DB.onCreate(db)

    default.forEach {
        DB.insertRecord(it)
    }
}
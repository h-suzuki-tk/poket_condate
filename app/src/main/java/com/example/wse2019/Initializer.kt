package com.example.sample

import android.content.ContentValues.TAG
import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.wse2019.csvImportHelper

//db作り直しからデフォルト値入れるまで
fun test_initializer(context: Context) {
    Log.d("program check", "initialize start")
    val db = SampleDBOpenHelper(context).writableDatabase
    val DB = SampleDBOpenHelper(context)

    DB.onCreate(db)

    default.forEach {
        DB.insertRecord(it)
    }
    csvImportHelper(context,DB,"Ingredients/01-06.csv")
    csvImportHelper(context,DB,"Ingredients/014.csv")
    csvImportHelper(context,DB,"Ingredients/011.csv")
    csvImportHelper(context,DB,"Ingredients/015.csv")
    csvImportHelper(context,DB,"Ingredients/016.csv")
    csvImportHelper(context,DB,"Ingredients/017.csv")
    csvImportHelper(context,DB,"Ingredients/018.csv")
    csvImportHelper(context,DB,"Ingredients/007.csv")
    csvImportHelper(context,DB,"Ingredients/008.csv")
    csvImportHelper(context,DB,"Ingredients/009.csv")
    csvImportHelper(context,DB,"Ingredients/010.csv")
    csvImportHelper(context,DB,"Ingredients/012.csv")
    csvImportHelper(context,DB,"Ingredients/013.csv")
    default_after.forEach {
        DB.insertRecord(it)
    }

    DB.dropTables(db)
}

//db初期化
fun initializer(context: Context) {
    Log.d("program check", "initialize start")
    val DB = SampleDBOpenHelper(context)
    val db = DB.writableDatabase

    Log.d("program check", "create table start")
    try {
        //テーブルの作成
        db.execSQL(createIngredientTable())
        db.execSQL(createFoodTable())
        db.execSQL(createRecordTable())
        db.execSQL(createMyCondateTable())
        db.execSQL(createCategoryTable())
        db.execSQL(createFoodIngredientsTable())
        db.execSQL(createMyCondateFoodTable())
        db.execSQL(createUserInfoTable())
        Log.d("program check", "create table done")

        // デフォルトデータの格納
        defaultData(context, DB)
    } catch  (ex: SQLiteException) {
        Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
        db.close()
        return
    }
}

//dbにデフォルト値を代入
fun defaultData(context: Context, DB: SampleDBOpenHelper) {
    Log.d("program check", "default making Start")

    default.forEach {
        DB.insertRecord(it)
    }
    csvImportHelper(context,DB,"Ingredients/01-06.csv")
    csvImportHelper(context,DB,"Ingredients/014.csv")
    csvImportHelper(context,DB,"Ingredients/011.csv")
    csvImportHelper(context,DB,"Ingredients/015.csv")
    csvImportHelper(context,DB,"Ingredients/016.csv")
    csvImportHelper(context,DB,"Ingredients/017.csv")
    csvImportHelper(context,DB,"Ingredients/018.csv")
    csvImportHelper(context,DB,"Ingredients/007.csv")
    csvImportHelper(context,DB,"Ingredients/008.csv")
    csvImportHelper(context,DB,"Ingredients/009.csv")
    csvImportHelper(context,DB,"Ingredients/010.csv")
    csvImportHelper(context,DB,"Ingredients/012.csv")
    csvImportHelper(context,DB,"Ingredients/013.csv")
    default_after.forEach {
        DB.insertRecord(it)
    }
    Log.d("program check", "default making ended")
}

fun dropTables(context: Context){
    val db = SampleDBOpenHelper(context).writableDatabase
    val DB = SampleDBOpenHelper(context)

    DB.dropTables(db)
}
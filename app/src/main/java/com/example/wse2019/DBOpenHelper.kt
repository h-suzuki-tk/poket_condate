package com.example.sample

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.renderscript.Sampler
import android.support.v4.widget.TintableImageSourceView
import android.util.Log
import java.util.concurrent.locks.Condition

private const val DB_NAME = "FoodManage"
private const val DB_VERSION = 1

class SampleDBOpenHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        //テーブルの作成
        Log.d("program check", "create table start")
        db?.execSQL(createIngredientTable())
        db?.execSQL(createFoodTable())
        db?.execSQL(createRecordTable())
        db?.execSQL(createMyCondateTable())
        db?.execSQL(createCategoryTable())
        db?.execSQL(createFoodIngredientsTable())
        db?.execSQL(createMyCondateFoodTable())
        db?.execSQL(createUserInfoTable())

        Log.d("program check", "create table done")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //バージョン更新時のSQL発行
        onCreate(db)

        Log.d("program check", "upgrade done")
    }

    private fun insertIngredient(ingredient: Table.Ingredient): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {
            put(DBContract.Ingredient.NAME, "'${ingredient.name}'")
            put(DBContract.Ingredient.SUGAR, ingredient.sugar)
            put(DBContract.Ingredient.FAT, ingredient.fat)
            put(DBContract.Ingredient.PROTEIN, ingredient.protein)
            put(DBContract.Ingredient.VITAMIN, ingredient.vitamin)
            put(DBContract.Ingredient.MINERAL, ingredient.mineral)
            put(DBContract.Ingredient.FIBER, ingredient.fiber)
            put(DBContract.Ingredient.CALORIE, ingredient.calorie)
            put(DBContract.Ingredient.QUANTITY, "'${ingredient.quantity}'")
            put(DBContract.Ingredient.UNIT, ingredient.unit)
            put(DBContract.Ingredient.ALLERGEN, ingredient.allergen)
        }

        try {
            //データベースに挿入する
            db.insert(DBContract.Ingredient.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }
        return true
    }

    private fun insertFood(food: Table.Food): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {

            put(DBContract.Food.NAME, "'${food.name}'")
            put(DBContract.Food.FAVORITE, food.favorite)
            put(DBContract.Food.MEMO, "'${food.memo}'")
            put(DBContract.Food.CATEGORY, food.category)
        }

        try {
            //データベースに挿入する
            db.insert(DBContract.Food.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }
        return true
    }


    private fun insertRecords(record: Table.Record): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {
            put(DBContract.Record.FOOD_ID, record.food_id)
            put(DBContract.Record.YEAR, record.year)
            put(DBContract.Record.MONTH, record.month)
            put(DBContract.Record.DATE, record.date)
            put(DBContract.Record.TIME, record.time)
        }
        try {
            //データベースに挿入する
            db.insert(DBContract.Record.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }
        return true
    }

    private fun insertMyCondate(myCondate: Table.MyCondate): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {
            put(DBContract.MyCondate.NAME, myCondate.name)
        }
        try {
            //データベースに挿入する
            db.insert(DBContract.MyCondate.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }
        return true
    }

    private fun insertCategory(category: Table.Category): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {
            put(DBContract.Category.NAME, "'${category.name}'")
            put(DBContract.Category.HIGHER_ID, category.higher_id)
        }

        try {
            //データベースに挿入する
            db.insert(DBContract.Category.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }

        return true
    }

    private fun insertFood_Ingredient(food_Ingredient: Table.Food_Ingredient): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {
            put(DBContract.Foods_Ingredients.FOOD_ID, food_Ingredient.food_id)
            put(DBContract.Foods_Ingredients.INGREDIENT_ID, food_Ingredient.Ingredient_id)
            put(DBContract.Foods_Ingredients.NUMBER, food_Ingredient.num)
        }
        //データベースに挿入する
        try {
            db.insert(DBContract.Foods_Ingredients.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }

        return true
    }

    private fun insertMyCondate_Food(myCondate_Food: Table.MyCondate_Food): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val record = ContentValues().apply {
            put(DBContract.MyCondate_Foods.MYCONDATE_ID, myCondate_Food.MyCondate_id)
            put(DBContract.MyCondate_Foods.FOOD_ID, myCondate_Food.food_id)
            put(DBContract.MyCondate_Foods.NUMBER, myCondate_Food.num)
        }
        //データベースに挿入する
        try {
            db.insert(DBContract.MyCondate_Foods.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }
        return true
    }

    //データのへの挿入を行う関数
    //TableModelにあるクラスとして変数を受け取り
    //結果はtrueかfalseで返す。
    //実際のインサート処理は各テーブルごとに上記の別の関数で行っている。
    fun insertRecord(table: Table): Boolean {
        val result: Boolean =
            when (table) {
                is Table.Ingredient -> insertIngredient(table)
                is Table.Food -> insertFood(table)
                is Table.Record -> insertRecords(table)
                is Table.MyCondate -> insertMyCondate(table)
                is Table.Category -> insertCategory(table)
                is Table.Food_Ingredient -> insertFood_Ingredient(table)
                is Table.MyCondate_Food -> insertMyCondate_Food(table)
                is Table.UserInfo -> false
            }

        return result
    }

    //テーブル検索
    // 変数：テーブル名、抽出するコラム、where句、ブレースホルダの値(条件の変数)、
    //       グルーピング条件、having、 order、 数制限
    // 返す型：List<String>
    // テーブル名以降は全て省略可能。その場合の動作はざっくり「SELECT * FROM TABLE_NAME」という全抽出
    // where句と条件の変数の書き方は、例えば名前が"さんまの塩焼き"であるレコードを探す場合、
    // condition = "name -> ?"、selectionArgs = arrayOf("'さんまの塩焼き'")となる。詳しくはtest_1st.ktにも。
    fun searchRecord(tableName: String, column: Array<String>? = null, condition: String? = null, selectionArgs: Array<String>? = null,
                     group: String? = null, having: String? = null, order: String? = null, limit:String? = null): List<String>/*?*//*(null対応用)*/{

        //読み込み可能なデータベースを開く
        val db = readableDatabase

        //データベースから検索を行う
        val cursor: Cursor?
        try {
            cursor = db.query(tableName, column, condition, selectionArgs, group, having, order, limit)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空の文字列を返す。
            //エラー文を添えることが出来ればなおよい
            //エラー時の返す値は他の関数とそろえる
            //テーブルの間違い、カラムの間違い党も表示できるとよい
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
//            return null (null対応用)
            return emptyList()
        }

        val results = mutableListOf<String>()   //返す文字リスト
        //コラム指定が無かった場合はテーブルのコラムの数だけ取得
        cursor.use {
            while (cursor.moveToNext()) {
                if(column == null) {
                    //コラム指定がなかった場合は全コラムを格納
                    for (i in 0 until cursor.columnCount) {
                        if (cursor.getString(i) != null) {
                            val result : String = cursor.getString(i)
                            results.add(result)
                        } else {
                            //データがnullだった場合はから文字列を入れておく。
                            results.add("")
                        }
                    }
                } else {
                    //コラム指定された場合は、指定されたコラムを確認してresultsに格納
                    column.forEach {
                        if (cursor.getString(cursor.getColumnIndex(it)) != null) {
                            val result: String = cursor.getString(cursor.getColumnIndex(it))
                            results.add(result)
                        } else {
                            //データがnullだった場合はから文字列を入れておく。
                            results.add("")
                        }
                    }
                }
            }
        }

        return results
    }
    //searchRecord終わり


    //データの更新を行う関数
    //変数は(テーブル名、変更するコラム(array)、新たに挿入するデータ(array)、条件(string)、条件(selectionArgs))
    //例えば、
    // updateRecord("Food", arrayOf("name", "calorie"), arrayOf("アユの塩焼き", "410f"), "name -> ?", arrayOf("さんまの塩焼き"))
    //では、name=='さんまの塩焼き'のレコードの食品名とカロリーをそれぞれアユの塩焼き、410カロリーと書き換えている
    fun updateRecord(tablename: String, column: Array<String>, convert: Array<String>, condition: String, selectionArgs : Array<String>) : Boolean{
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        val update = ContentValues().apply{
            for(i in 0 until column.size-1){
                put(column[i], convert[i])
            }
        }

        try {
            db.update(tablename, update, condition, selectionArgs)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空のFALSEを返す。
            //エラー文を添えることが出来ればなおよい
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }

        //更新に成功したのでTRUEを返す
        return true
    }

    //データの削除を行う関数
    //変数は(テーブル名、削除するレコードの条件(String)、条件(selectionArgs))
    //例えば、
    //deleteRecord("Food", "name -> ? AND name -> ?", Array("カルボナーラ", "ジェノベーゼ"))
    //では、Fooｄテーブルの名前がカルボナーラかジェノベーゼのレコードを破壊する
    fun deleteRecord(tablename : String, condition: String, selectionArgs : Array<String>) : Boolean{
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        try {
            db.delete(tablename, condition, selectionArgs)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空のFALSEを返す。
            //エラー文を添えることが出来ればなおよい
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            return false
        }

        //削除に成功したのでTRUEを返す
        return true
    }

    //テーブル全削除機能
    //主にテストや初期化、更新用
    fun dropTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Ingredient.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Food.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Record.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.MyCondate.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Category.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.Foods_Ingredients.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.MyCondate_Foods.TABLE_NAME)
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.UserInfo.TABLE_NAME)
    }
}

//テーブル名からそのテーブルのコラム数が欲しいを返す関数
//もっとスマートに作りたい
fun columnNum(tableName: String) : Int {
    val num : Int =
        when(tableName) {
            DBContract.Ingredient.TABLE_NAME -> 12
            DBContract.Food.TABLE_NAME -> 5
            DBContract.Record.TABLE_NAME -> 6
            DBContract.MyCondate.TABLE_NAME -> 2
            DBContract.Category.TABLE_NAME -> 3
            DBContract.Foods_Ingredients.TABLE_NAME -> 3
            DBContract.MyCondate_Foods.TABLE_NAME -> 3
            DBContract.UserInfo.TABLE_NAME -> 6
            else -> 0
        }
    return num
}
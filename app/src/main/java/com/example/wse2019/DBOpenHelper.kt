package com.example.sample

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.wse2019.csvImportHelper

private const val DB_NAME = "FoodManage"
private const val DB_VERSION = 1

class SampleDBOpenHelper(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    val context = context

    override fun onCreate(db: SQLiteDatabase) { }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
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
            put(DBContract.Ingredient.CLASS, ingredient.clas)
        }

        try {
            //データベースに挿入する
            db.insert(DBContract.Ingredient.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return false
        }
        db.close()
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
            db.close()
            return false
        }

        db.close()
        return true
    }


    private fun insertRecords(record: Table.Record): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        //挿入するレコード
        val records = ContentValues().apply {
            put(DBContract.Record.FOOD_ID, record.food_id)
            put(DBContract.Record.YEAR, record.year)
            put(DBContract.Record.MONTH, record.month)
            put(DBContract.Record.DATE, record.date)
            put(DBContract.Record.TIME, record.time)
            put(DBContract.Record.NUMBER, record.number)
        }
        try {
            //データベースに挿入する
            db.insert(DBContract.Record.TABLE_NAME, null, records)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return false
        }

        db.close()
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
            db.close()
            return false
        }

        db.close()
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
            db.close()
            return false
        }

        db.close()
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
            db.close()
            return false
        }

        db.close()
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
            db.close()
            return false
        }

        db.close()
        return true
    }

    private fun insertUserInfo(userInfo: Table.UserInfo): Boolean {
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        val record = ContentValues().apply {
            put(DBContract.UserInfo.NAME, userInfo.name)
            put(DBContract.UserInfo.HEIGHT, userInfo.height)
            put(DBContract.UserInfo.WEIGHT, userInfo.weight)
            put(DBContract.UserInfo.AGE, userInfo.age)
            put(DBContract.UserInfo.SEX, userInfo.sex)
        }

        //データベースに挿入する
        try {
            db.insert(DBContract.UserInfo.TABLE_NAME, null, record)
        } catch (ex: SQLiteException) {
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return false
        }

        db.close()
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
                is Table.UserInfo -> insertUserInfo(table)
                else -> false
            }

        return result
    }

    // searchRecordとsearchRecord_dicの同じ処理で長ったらしいため
    // こちらに統一
    // (SQL文だけ得たとこでできること少ないだろうし多分private外すことは)ないです
    private fun getSQL(tableName: String, column: Array<String>? = null, condition: String? = null, selectionArgs: Array<String>? = null,
                       group: String? = null, having: String? = null, order: String? = null, limit:String? = null, innerJoin: Join? = null,
                       multiJoin: Array<Join>? = null): String{
        Log.d("select", "start")

        // db.queryによるDB接続はJoinに対応してない(多分。。。)ため、
        // 面倒ですがセレクトはクエリ文を作って実行します。
        // ここでは先頭から単語を徐々に追加していく感じでクエリ文作っていきます。
        var sql = "SELECT"

        //抽出するコラムをクエリ文に追加
        if (column != null) {
            var columnHead: Boolean = true   //先頭か否かで前コンマの有無を決めるため
            column.forEach {
                if (columnHead) {
                    sql += " $it"
                    columnHead = false
                } else {
                    sql += ", $it"
                }
            }
        } else {
            //コラム指定がnullの場合は*、つまり全コラムを抽出します。
            sql += " *"
        }

        if(group != null) {
            sql += ", count(*)"
        }

        //FROMと検索するテーブル名を追加
        if (multiJoin == null) {
            sql += " FROM $tableName"
        } else {
            var joinHead: Boolean = true
            var joinSql: String = tableName

            multiJoin.forEach {
                val table1 = it.connected ?: tableName
                val table2 = it.tablename

                if (joinHead) {
                    joinSql = "$joinSql INNER JOIN $table2 ON $table1.${it.column1} = $table2.${it.column2}"
                    joinHead = false
                } else {
                    joinSql = "($joinSql) INNER JOIN $table2 ON $table1.${it.column1} = $table2.${it.column2}"
                }
            }

            sql += " FROM $joinSql"
//            Log.d("join check", sql)
        }

        // 内部結合が指定された場合はここでJoin文の作成・追加を行う。
        // innerJoinとmultiJoin両方の発動はエラーになるんで防いどきます。
        if (innerJoin != null && multiJoin == null) {
            sql += " INNER JOIN ${innerJoin.tablename} ON $tableName.${innerJoin.column1} = ${innerJoin.tablename}.${innerJoin.column2}"
        }

        //条件が指定されている場合はここでWHERE文の作成・追加を行う。
        if (condition != null) {
            sql += " WHERE $condition"
        }

        // グルーピングを行うカラムの指定
        if(group != null) {
            sql += " GROUP BY $group"
        }

        // グルーピングを行った上でそれらを絞る
        if(having != null) {
            sql += " HAVING $having"
        }

        return sql
    }


    //テーブル検索
    // 変数：テーブル名、抽出するコラム、where句、ブレースホルダの値(条件の変数)、
    //       グルーピング条件、having、 order、 数制限
    // 返す型：List<String>
    // テーブル名以降は全て省略可能。その場合の動作はざっくり「SELECT * FROM TABLE_NAME」という全抽出
    // where句と条件の変数の書き方は、例えば名前が"さんまの塩焼き"であるレコードを探す場合、
    // condition = "name -> ?"、selectionArgs = arrayOf("'さんまの塩焼き'")となる。詳しくはtest_1st.ktにも。
    fun searchRecord(tableName: String, column: Array<String>? = null, condition: String? = null, selectionArgs: Array<String>? = null,
                     group: String? = null, having: String? = null, order: String? = null, limit:String? = null, innerJoin: Join? = null,
                     multiJoin: Array<Join>? = null): List<String>? {
        val db = readableDatabase

        val sql =
            getSQL(tableName, column, condition, selectionArgs,
                group, having, order, limit, innerJoin, multiJoin) ?: return null

        //データベースから検索を行う
        val cursor: Cursor?
        try {
            // db.query機能どこまで使っているか分からないので一応取っありてます。
            // 前述した通り、内部結合には対応していないため使うか否かでかき分けておきますが、
            // まぁ望ましくないので徐々に全機能をクエリ文作成のほうに統一していきます。
            cursor = db.rawQuery(sql, selectionArgs)
//            Log.d("check", sql)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空の文字列を返す。
            //エラー文を添えることが出来ればなおよい
            //エラー時の返す値は他の関数とそろえる
            //テーブルの間違い、カラムの間違い等も表示できるといいなぁ
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return null
        }

        val results = mutableListOf<String>()   //返す文字リスト
        //コラム指定が無かった場合はテーブルのコラムの数だけ取得
        cursor.use {
            while (cursor.moveToNext()) {
                for (i in 0 until cursor.columnCount) {
                    var result: String = cursor.getString(i) ?: ""

                    // 前後の「'」をカット
                    val regex = Regex(pattern = """'.*'""")
                    if (regex.matches(input = result)) {
                        result = result.substring(1, result.length-1)
                    }

                    results.add(result)
                }
            }
        }

        db.close()
        println("searchRecord success\n")
        return results
    }
    //searchRecord終わり

    //テーブル検索
    // 変数：テーブル名、抽出するコラム、where句、ブレースホルダの値(条件の変数)、
    //       グルーピング条件、having、 order、 数制限
    // 返す型：List<Dictionary>
    // 基本的な操作ははsearchRecordと同じ
    // ただし、List<String>で返すあちらと異なりList<Dictionary(データリスト、フィールド名)>で返す
    // 特に複数のカラムを抽出して一つ一つ分別したいときはこちらのがいいと思います。　
    fun searchRecord_dic(tableName: String, column: Array<String>? = null, condition: String? = null, selectionArgs: Array<String>? = null,
                         group: String? = null, having: String? = null, order: String? = null, limit:String? = null, innerJoin: Join? = null,
                         multiJoin: Array<Join>? = null): List<Dictionary>? {
        val db = readableDatabase
        val dic = mutableListOf<Dictionary>()
        if (column != null) {
            column.forEach {
                dic.add(Dictionary(mutableListOf(), it))
            }
        } else {
            selectTable(tableName).getColumn().forEach {
                dic.add(Dictionary(mutableListOf(), it))
            }
        }

        if(group != null) {
            dic.add(Dictionary(mutableListOf(), "count"))
        }

        val sql =
            getSQL(tableName, column, condition, selectionArgs,
                group, having, order, limit, innerJoin, multiJoin) ?: return null

        //データベースから検索を行う
        val cursor: Cursor?
        try {
            // db.query機能どこまで使っているか分からないので一応取っありてます。
            // 前述した通り、内部結合には対応していないため使うか否かでかき分けておきますが、
            // まぁ望ましくないので徐々に全機能をクエリ文作成のほうに統一していきます。
            cursor = db.rawQuery(sql, selectionArgs)
//            Log.d("check", sql)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空の文字列を返す。
            //エラー文を添えることが出来ればなおよい
            //エラー時の返す値は他の関数とそろえる
            //テーブルの間違い、カラムの間違い等も表示できるといいなぁ
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return null
        }

        //コラム指定が無かった場合はテーブルのコラムの数だけ取得
        cursor.use {
            while (cursor.moveToNext()) {
                //コラム指定がなかった場合は全コラムを格納

                var num = 0
                dic.forEach {
                    val result: String = cursor.getString(num) ?: ""
                    it.data.add(result)
                    num++
                }

            }
        }
        //List<Dictionary>?で返す

        println("searchRecord_dic success\n")
        db.close()
        return dic
    }
    //searchRecord_dic終わり

    //データの更新を行う関数
    //変数は(テーブル名、変更するコラム(array)、新たに挿入するデータ(array)、条件(string)、条件(selectionArgs))
    //例えば、
    // updateRecord("Food", arrayOf("name", "calorie"), arrayOf("アユの塩焼き", "410f"), "name -> ?", arrayOf("さんまの塩焼き"))
    //では、name=='さんまの塩焼き'のレコードの食品名とカロリーをそれぞれアユの塩焼き、410カロリーと書き換えている
    fun updateRecord(tablename: String, column: Array<String>, convert: Array<String>, condition: String, selectionArgs : Array<String>) : Boolean{
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        Log.d("program check", "update start")
        val update = ContentValues().apply{
            for(i in 0 until column.size){
                put(column[i], convert[i])
                Log.d("update check", "$column[i], $convert[i]")
            }
        }

        try {
            db.update(tablename, update, condition, selectionArgs)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空のFALSEを返す。
            //エラー文を添えることが出来ればなおよい
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return false
        }

        //更新に成功したのでTRUEを返す
        Log.d("program check", "update success")
        db.close()
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
            db.close()
            return false
        }

        //削除に成功したのでTRUEを返す
        db.close()
        return true
    }

    fun deleteRecord(tablename : String, condition: String) : Boolean{
        //書き込み可能なデータベースを開く
        val db = writableDatabase

        try {
            db.delete(tablename, condition, null)
        } catch (ex: SQLiteException) {
            //クエリ文が失敗した場合は空のFALSEを返す。
            //エラー文を添えることが出来ればなおよい
            Log.e(TAG, "SQLite execution failed" + ex.localizedMessage)
            db.close()
            return false
        }

        //削除に成功したのでTRUEを返す
        db.close()
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
        db.execSQL("DROP DATABASE IF EXISTS " + DB_NAME)
        println("drop success")
    }
}

// searchSelectを内部結合に対応させるために用いるクラス
// searchRecordを呼び出す際に関数の指定に(TABLE_NAME, COLUMNS, innerJoin = 「Joinクラス」という形で呼び出してください)
// 第1変数:結合先テーブル名、第2変数:結合する第１テーブルのコラム、第3変数:結合する第2テーブルのコラム
class Join(val tablename: String, val column1: String, val column2: String, val connected: String? = null)

// searchRecordを辞書型で返すためのクラス
// 第１変数dataはデータのリスト、第2変数はフィールド(カラム名)
// searchRecord_dicは、検索結果をこのクラスのリストで返す。
// 例えば。パスタに絞って品目の名前を抽出した場合、
// Dictionary(listOf("ペペロンチーノ", "カルボナーラ". "ミートスパ", "和風醤油バター", "ジェノベーゼ"), "name")
// という感じ、これをsizeがカラム数のリストとして返します。わかりづらいかな。。。？
class StrDic(val data: MutableList<String>, val field: String)  // Dictionaryと同じ、見栄え気にするなら使えばいいと思います。

// DictionaryのIntバージョン
class IntDic(val data: MutableList<Int>, val field: String){

}

// DictionaryのFloatバージョン
class FloatDic(val data: MutableList<Float>, val field: String){
    var sum = 0f
    // データの数値の合計をFloat型で返す機能
    // 変数はそれぞれの倍率をいじれる様に
    // つまり、品目材料テーブルの数量に対応させるため
    // 使わなけりゃFloatDic.sum()で構わない
    fun sum(quant : List<Float>? = null): Float{
        if(quant == null) {
            // 単純に合計したいだけなら
            data.forEach {
                sum += it
            }
        } else {
            // それぞれの数量が指定された場合はこっち
            var num = 0
            data.forEach {
                sum += it*quant[num]
                num++
            }
        }
        return sum
    }

    // データの数値の平均をFloat型で返す機能
    fun average(): Float{
        data.forEach {
            sum += it
        }
        return sum/data.size
    }
}

open class Dictionary(val data: MutableList<String>, val field: String){

    // 補助その1
    // 辞書型のデータをIntに変換する
    // 変換に失敗した場合はとりあえず0を入れておく。
    fun toInt(): IntDic{
        val intDic = IntDic(mutableListOf(), field)
        data.forEach{
            try {
                intDic.data.add(it.toInt())
            } catch(e: Exception) {
                intDic.data.add(0)
            }
        }
        return intDic
    }

    // 補助その2
    // 辞書型のデータをFloatに変換する
    // 変換に失敗した場合はとりあえず0fを入れておく。
    fun toFloat(): FloatDic{
        val floatDic = FloatDic(mutableListOf(), field)
        data.forEach{
            try {
                floatDic.data.add(it.toFloat())
            } catch(e: Exception) {
                floatDic.data.add(0f)
            }
        }
        return floatDic
    }

    // 補助その3(順番前後しました)
    // ほぼ見栄え、見易さ用
    fun toStr(): StrDic{
        return StrDic(data, field)
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
package com.example.sample

import android.content.Context
import android.util.Log

fun test1(context: Context) {
    Log.d("program check", "test start")
    val db = SampleDBOpenHelper(context).writableDatabase
    val DB = SampleDBOpenHelper(context)

    DB.dropTables(db)
    DB.onCreate(db)

    //適当にinsertをやってみる。
    //とりあえず材料に「お米」と「いくら」入れて、
    //それらが合体した「いくらご飯」を作ってみます。うまそう
    //現在は小数はfloatを用いているため語尾にfを付けるように
    //各テーブルのデータ型はTableModel.ktを参考に。7/8現在null許容部分が多いです。
    DB.insertRecord(
        Table.Ingredient(
            "お米", 38.1f, 0.3f, 3.5f, 0f, 0f,
            0f, 168f, 100f, "グラム",0
        )
    )

    DB.insertRecord(
        Table.Ingredient(
            "いくら", 0.12f, 9.36f, 19.56f, null, 0f,
            null, 163f, 60f, "グラム",1
        )
    )

    //変数を使ったり使わなかったりで検索を実行している。
    //searchRecordでは、頭から「テーブル名」、「求めるコラム(array<String>)」、「条件文」、「条件の変数」となっている。
    //後半の2つは省略可能で、その場合は全ての値が返される。
    //また、この関数で返すのは列がいくつあろうが型が何であろうがとlist<String>なので、変換を忘れないように
    val columns = arrayOf("name")
    val result1 = DB.searchRecord("ingredients", columns)
    result1.forEach {
        Log.d("result1", it)
    }

    //上と似たような操作
    //変数ではなく直接値を指定しているくらい
    val result2 = DB.searchRecord("ingredients", arrayOf("name"))
    result2.forEach {
        Log.d("result2", it)
    }
    //おなじみ楽々検索の「select * from "table_name"」の形
    //要は全コラムの抽出です。
    val result3 = DB.searchRecord("ingredients", arrayOf("*"))
    result3.forEach {
        Log.d("id", it)
    }


    val makeFood = "いくらご飯"
    DB.insertRecord(
        Table.Food(makeFood, 0, null, 8)
    )
    val result4 = DB.searchRecord("foods", arrayOf("*"))
    result4.forEach {
        Log.d("result4", it)
    }


    //これも同じような操作だが、DBContractで設定した値を用いている。
    //多少入力が面倒かもしれないが、一番安全。
    //なんなら、後でテーブル名カラム名いじくり回すかもしれないので、
    // 基本的にテーブルの操作にはDBContract使ってください。
    val oldFood = DB.searchRecord(DBContract.Food.TABLE_NAME, arrayOf(DBContract.Food.ID))
    oldFood.forEach {
        Log.d("food", it)
    }

    //さっきまでのに加えて条件を設定した検索
    //conditionには「?」を用いた条件文を入れ、selectionArgsには「?」に入れる値もしくは変数を渡す。
    //下の例では、つまり「name = 'いくらご飯'」という条件を設定している。
    //「where」の追加は関数でやっているので考えなくてもよい
    val newFood = DB.searchRecord(DBContract.Food.TABLE_NAME, arrayOf(DBContract.Food.ID),
        "name = ?", arrayOf("'いくらご飯'"))

    //Listから文字列を抽出する一例。
    //このフードIDは後でリレーションに使います。
    val foodID : Int = Integer.parseInt(newFood.get(0))

    //ここで設定された条件は、「name = 'お米' or name = 'いくら'」である。
    //つまり、「いくらご飯」の材料のIDを一気に検索してる。
    val ingredientList = DB.searchRecord(DBContract.Ingredient.TABLE_NAME, arrayOf(DBContract.Ingredient.ID),
        "name = ? or name = ?", arrayOf("'お米'", "'いくら'"))

    ingredientList.forEach {
        Log.d("List", it)
    }

    //いくらご飯の材料IDが「ingredientList」に集まったので
    //forEach文を使ってFood_Ingredientテーブルに入れちゃいましょう
    //先ほどfoodIDも確保済みで不変なので楽
    ingredientList.forEach {
        val ingredientID : Int = Integer.parseInt(it)
        DB.insertRecord(Table.Food_Ingredient(foodID, ingredientID, 1))
    }

    //ここまでの操作で「いくらご飯」、まぁいくら丼が完成したはずです。
    //確認のため検索
    val last = DB.searchRecord(DBContract.Foods_Ingredients.TABLE_NAME, arrayOf(DBContract.Foods_Ingredients.INGREDIENT_ID),
        "${DBContract.Foods_Ingredients.FOOD_ID} = ?", arrayOf("$foodID"))

    last.forEach {
        val last_id : Int = Integer.parseInt(it)
        val IngredientName = DB.searchRecord(DBContract.Ingredient.TABLE_NAME, arrayOf(DBContract.Ingredient.NAME),
            "${DBContract.Ingredient.ID} = ?", arrayOf("$last_id"))

        Log.d("last", IngredientName.get(0))
    }
    //多分何かが変わってなければ'お米''いくら'の2つの単語が検索されるはずです。
    //ざっくりとした使い方はこんな感じです。
    //まだまだ不便な箇所が多くて申し訳ないですが、よろぴくです。
}
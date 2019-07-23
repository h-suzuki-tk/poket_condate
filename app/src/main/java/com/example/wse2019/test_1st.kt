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

    // 変数を使ったり使わなかったりで検索を実行している。
    // searchRecordでは、頭から「テーブル名」、「求めるコラム(array<String>)」、「条件文」、「条件の変数」、「検索の方式」あれこれ
    // となっている。
    // テーブル名以外は省略可能で、その場合は全ての値が返される。(つまりは「select * from TABLE_NAME」の形になる)
    // また、現在この関数で返すのは列がいくつあろうが型が何であろうがとlist<String>なので、変換を忘れないように

    // 下の例は材料テーブルから'name'のコラムを抽出したもの
    // つまり材料テーブルの持つ材料名一覧が返される形になる。
    val column = arrayOf("name")
    val result1 = DB.searchRecord("ingredients", column)
    result1.forEach {
        Log.d("result1", it)
    }

    // カラムをわざわざ配列に指定してるあたり当たり前だが、抽出するカラムは複数でも可能
    // 上と似たような操作だが今回は材料テーブルの持つ材料名とカロリーが抽出される。
    val columns = arrayOf("name", "calorie")
    val result2 = DB.searchRecord("ingredients", columns)
    result2.forEach {
        Log.d("result2", it)
    }

    // 繰り返しになるが、コラムを全抽出する場合は第二変数にnullを与えればよい
    // 第２変数はデフォルトでnullに設定してあるので、テーブル名だけ渡す形でも可
    // searchRecord("ingredients", null)

    // 下の三つは全て同じ動作である。
    // result5のようなことはさすがにしないだろうが
    val result3 = DB.searchRecord("ingredients")
    val result4 = DB.searchRecord("ingredients", null)
    val result5 = DB.searchRecord("ingredients", arrayOf("id", "name", "sugar", "fat", "protein", "vitamin",
                                                                     "mineral", "fiber", "calorie", "quantity", "unit", "allergen"))

    result3.forEach {
        Log.d("id", it)
    }


    val makeFood = "いくらご飯"
    DB.insertRecord(
        Table.Food(makeFood, 0, null, 8)
    )
    val resulter = DB.searchRecord("foods")
    resulter.forEach {
        Log.d("result4", it)
    }


    // 実際テーブル名やカラム名を指定する場合は、直接入れずにDBContractを参照してください。
    // 多少入力が面倒かもしれないが、一番安全。
    // 後でテーブル名やカラム名いじくり回すかもしれないので。
    val oldFood = DB.searchRecord(DBContract.Food.TABLE_NAME, arrayOf(DBContract.Food.ID))
    oldFood.forEach {
        Log.d("food", it)
    }

    // さっきまでのに加えてwhere句(条件)を設定した検索
    // 第三変数conditionには「?」を用いた条件文を入れ、第四変数selectionArgsには「?」に入れる値もしくは変数を渡す。
    // 下の例では、つまり「name = 'いくらご飯'」という条件を設定している。
    val newFood = DB.searchRecord(DBContract.Food.TABLE_NAME, arrayOf(DBContract.Food.ID),
        "name = ?", arrayOf("'いくらご飯'"))

    //Listから文字列を抽出する一例。
    //このフードIDは後でリレーションに使います。
    val foodID : Int = Integer.parseInt(newFood.get(0))

    // もちろん条件も複数の指定は可能である。
    // 例えば、下で設定された条件は「name = 'お米' or name = 'いくら'」、
    // つまり材料名が'お米'あるいわ'いくら'であるレコードのIDを返す形になる。
    val ingredientList = DB.searchRecord(DBContract.Ingredient.TABLE_NAME, arrayOf(DBContract.Ingredient.ID),
        "name = ? or name = ?", arrayOf("'お米'", "'いくら'"))


    // 条件を担当するのは第三変数と第四変数と決まっているので、
    // 条件をwhere句を加える場合は全カラム抽出であっても第二変数は省略せずnullを指定するように。
    // 不正であるためコメントアウトしているが、下のNGの表記は間違い
    // val NG = DB.searchRecord((DBContract.Ingredient.TABLE_NAME, "name = ? or name = ?", arrayOf("'お米'", "'いくら'"))
    // val OK = DB.searchRecord((DBContract.Ingredient.TABLE_NAME, null, "name = ? or name = ?", arrayOf("'お米'", "'いくら'"))


    // いくらご飯の材料IDが「ingredientList」に集まったので
    // forEach文を使ってFood_Ingredientテーブルに入れちゃいましょう
    // 先ほどfoodIDも確保済みで不変なので楽
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
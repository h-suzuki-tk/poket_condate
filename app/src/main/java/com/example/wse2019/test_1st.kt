package com.example.sample

import android.content.Context
import android.util.Log
import com.example.wse2019.NutritionHelper

fun test1(context: Context) {
    Log.d("program check", "test start")
    val DB = SampleDBOpenHelper(context)
    val db = DB.writableDatabase

    DB.dropTables(db)
    DB.onCreate(db)
    db.close()

    //適当にinsertをやってみる。
    //とりあえず材料に「お米」と「いくら」入れて、
    //それらが合体した「いくらご飯」を作ってみます。うまそう
    //現在は小数はfloatを用いているため語尾にfを付けるように
    //各テーブルのデータ型はTableModel.ktを参考に。7/8現在null許容部分が多いです。

    println("test1\n")

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
    result1?.forEach {
        Log.d("result1", it)
    }

    Log.d("test1", "success!")

    // カラムをわざわざ配列に指定してるあたり当たり前だが、抽出するカラムは複数でも可能
    // 上と似たような操作だが今回は材料テーブルの持つ材料名とカロリーが抽出される。
    val columns = arrayOf("name", "calorie")
    val result2 = DB.searchRecord("ingredients", columns)
    result2?.forEach {
        Log.d("result2", it)
    }

    Log.d("test2", "success!")


    // 繰り返しになるが、コラムを全抽出する場合は第二変数にnullを与えればよい
    // 第２変数はデフォルトでnullに設定してあるので、テーブル名だけ渡す形でも可
    // searchRecord("ingredients", null)

    // 下の三つは全て同じ動作である。
    // result5のようなことはさすがにしないだろうが
    val result3 = DB.searchRecord("ingredients")
    val result4 = DB.searchRecord("ingredients", null)
    val result5 = DB.searchRecord("ingredients", arrayOf("id", "name", "sugar", "fat", "protein", "vitamin",
        "mineral", "fiber", "calorie", "quantity", "unit", "allergen"))

    result3?.forEach {
        Log.d("id", it)
    }

    Log.d("test3", "success!")


    val makeFood = "いくらご飯"
    DB.insertRecord(
        Table.Food(makeFood, 0, null, 8)
    )
    val resulter = DB.searchRecord("foods")
    resulter?.forEach {
        Log.d("result4", it)
    }

    Log.d("test4", "success!")


    // 実際テーブル名やカラム名を指定する場合は、直接入れずにDBContractを参照してください。
    // 多少入力が面倒かもしれないが、一番安全。
    // 後でテーブル名やカラム名いじくり回すかもしれないので。
    val oldFood = DB.searchRecord(DBContract.Food.TABLE_NAME, arrayOf(DBContract.Food.ID)) ?: return
    oldFood.forEach {
        Log.d("food", it)
    }

    // さっきまでのに加えてwhere句(条件)を設定した検索
    // 第三変数conditionには「?」を用いた条件文を入れ、第四変数selectionArgsには「?」に入れる値もしくは変数を渡す。
    // 下の例では、つまり「name = 'いくらご飯'」という条件を設定している。
    val newFood = DB.searchRecord(DBContract.Food.TABLE_NAME, arrayOf(DBContract.Food.ID),
        "name = ?", arrayOf("'いくらご飯'")) ?: return

    //Listから文字列を抽出する一例。
    //このフードIDは後でリレーションに使います。
    val foodID : Int = Integer.parseInt(newFood.get(0))

    // もちろん条件も複数の指定は可能である。
    // 例えば、下で設定された条件は「name = 'お米' or name = 'いくら'」、
    // つまり材料名が'お米'あるいわ'いくら'であるレコードのIDを返す形になる。
    val ingredientList = DB.searchRecord(DBContract.Ingredient.TABLE_NAME, arrayOf(DBContract.Ingredient.ID),
        "name = ? or name = ?", arrayOf("'お米'", "'いくら'")) ?: return

    Log.d("test5", "success!")


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
        "${DBContract.Foods_Ingredients.FOOD_ID} = ?", arrayOf("$foodID")) ?: return

    last.forEach {
        val last_id : Int = Integer.parseInt(it)
        val IngredientName = DB.searchRecord(DBContract.Ingredient.TABLE_NAME, arrayOf(DBContract.Ingredient.NAME),
            "${DBContract.Ingredient.ID} = ?", arrayOf("$last_id"))

        Log.d("last", IngredientName?.get(0))
    }
    Log.d("test6", "success!")

    // 多分何かが変わってなければ'お米''いくら'の2つの単語が検索されるはずです。
    // ざっくりとした使い方はこんな感じです。
    // まだまだ不便な箇所が多くて申し訳ないですが、よろぴくです。

    initializer(context)
    val Food = DBContract.Food
    val Ingredient = DBContract.Ingredient
    val Food_Ingredient = DBContract.Foods_Ingredients
    // 内部結合使用例
    // searchSelectを用いる際、下のように「innerJoin = "Joinクラス"」を加えて使います。
    // Joinクラスは、第一引数につなげるテーブルの名前を指定、
    // 第二引数に元の被結合テーブルの共有するカラム名
    // 第三引数に新たに結合するテーブルの共有カラム名を渡してください
    // 第四引数はここでは使わないので省略して大丈夫です。
    // 今のところ抽出するコラムを指定する場合は各自で「テーブル名.カラム名」を作成してもらう形になっています。

    val join = Join(Food_Ingredient.TABLE_NAME, Food.ID, Food_Ingredient.FOOD_ID)
    val joincheck = DB.searchRecord(Food.TABLE_NAME, innerJoin = join) ?: return

    // 3つ以上の結合を行う際は、innerJoinは使わずmultiJoinを使います。
    // multiJoinは先ほど用いたJoinクラスの配列を受け取ります。
    // 使い方として、まず下のようなJoinクラスの配列を作ります。　
    val multiJoin = arrayOf(
        //品目材料テーブルを結合
        Join(Food_Ingredient.TABLE_NAME, Food.ID, Food_Ingredient.FOOD_ID),
        //材料テーブルを員目材料テーブルに結合
        Join(Ingredient.TABLE_NAME, Food_Ingredient.INGREDIENT_ID, Ingredient.ID, Food_Ingredient.TABLE_NAME)
    )
    // ここで、2つめのJoinクラスに注目すると、引数が4つあります。
    // これは、結合するテーブルを選択する場合に用いるものです。
    // 1つめのJoinクラスは、結合先がsearchRecordのtablenameに指定した品目テーブルなので省略していますが、
    // 2つめのJoinクラスでは、結合先が品目テーブルでなく品目材料テーブルになるので、指定する必要があります。

    // 「テーブル名.カラム名」のコレクションを作成
    val joinColumns = arrayOf(
        "${Food.TABLE_NAME}.${Food.NAME}",
        "${Ingredient.TABLE_NAME}.${Ingredient.NAME}"
    )

    val foodIng = DB.searchRecord(tableName = Food.TABLE_NAME, column = joinColumns, multiJoin = multiJoin) ?: return

    foodIng.forEach {
        Log.d("multiJoin", it)
    }

    Log.d("test7", "success!")


    // DBの検索結果を辞書型(といっても不完全なのでポスティングリストの寄せ集めっすね)
    // で受け取れるようにしました
    // クラスDictionary(data: List<String>, field: String)を用いるもので、
    // 第一引数はデータリスト、第二引数はフィールド名、つまりコラム名が入ります。
    // 例えば。パスタに絞って品目(Food)テーブルの名前を抽出した場合、
    // Dictionary(listOf("ペペロンチーノ", "カルボナーラ". "ミートスパ", "和風醤油バター", "ジェノベーゼ"), "name")
    // という感じです。　
    val Dic = DB.searchRecord_dic(Ingredient.TABLE_NAME, arrayOf(Ingredient.ID, Ingredient.NAME, Ingredient.CALORIE)) ?: return

    // この辞書クラスでは.(ドット)と続いて「toInt()」「toFloat()」「toStr()」を付けることで
    // それぞれの型に対応した型のデータクラスに変換することが出来ます。
    // 上ではカラム名にIDと名前を指定して検索しています。
    // つまり、1つ目はINT型、2つ目はString型が欲しいとすると以下のように　

    val ident = Dic[0].toInt()  //ident:クラスIntDoc(List<Int>, String)
    val names = Dic[1].toStr()  //names:クラスStrDoc(List<String>, String)
    val calorie = Dic[2].toFloat()  //calorie:クラスFloatDic(List<Float>, string)
    //とすれば、それぞれ型の異なる別のクラスに置き換えられます。toStr()の意味？見栄え見栄え

    ident.data.forEach{
        Log.d("ident", it.toString())
    }
    names.data.forEach {
        Log.d("names", it)
    }
    calorie.data.forEach{
        Log.d("calorie", it.toString())
    }


    // 材料から品目の栄養を計算するくらすNutritionを扱ってみる。
    val Nut = NutritionHelper(context)

    // まず、品目の名前からその品目の栄養素を算出するgetNutririonがあります。
    // まだ単体の計算なので、繰り返し事態は使用者に設定してもらう形です。
    val foodName = arrayOf(Food.NAME)
    val tryNut = DB.searchRecord_dic(Food.TABLE_NAME, foodName) ?: return
    // まず品目の名前のリストをNutDicに入れます。変数名むちゃくちゃでごめんね
    val NutDic = tryNut[0]
    NutDic.data.forEach{
        Log.d("nuttest", it)
    }

    // NutDic.dataが文字列のリストなので、
    // それをたどってgetNutritionを実行　
    NutDic.data.forEach {
        val thisNut = Nut.getNutrition(it) ?: return
        println("${thisNut.foodname} : ${thisNut.sugar}, ${thisNut.mineral}, ${thisNut.fiber}, ${thisNut.calorie} \n")
    }
    // Logcatに「"品目名" : 糖分、ミネラル、繊維、カロリー」という感じで出力されてると思います。
}
package com.example.wse2019

import android.content.Context
import android.util.Log
import com.example.sample.DBContract
import com.example.sample.Dictionary
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import kotlin.math.sqrt

// 栄養分析に用いるクラス。品目名とその材料から算出した栄養分の総量を引数にもっています。
class Nutrition(
    val foodname: String, val sugar: Float, val fat: Float, val protein: Float,
    val vitamin: Float, val mineral: Float, val fiber: Float, val calorie: Float, val foodID: Int? = null
) {
    fun contains(element: Float?): Boolean {
        return (sugar.equals(element)
                || fat      .equals(element)
                || protein  .equals(element)
                || vitamin  .equals(element)
                || mineral  .equals(element)
                || fiber    .equals(element)
                || calorie  .equals(element))
    }

    fun  normalization(): Nutrition{
        val sum = sqrt(sugar*sugar + fat*fat + protein*protein + vitamin*vitamin
                + mineral*mineral + fiber*fiber)

        return Nutrition(foodname, sugar/sum, fat/sum, protein/sum,
            vitamin/sum, mineral/sum, fiber/sum, calorie)
    }
}

class Result(val foodIDList: MutableList<Int>)

// 栄養分の計算や分析を担当するクラスNutritionHelperです。
// 基本的な構造はSQLiteOpenHelperと同じようにするつもりです。
// contextの扱いは今のところちょっと分かりかねるんで、
// 不便を感じていてかつもっといい方法があるという場合は教えていただけると幸いです。
class NutritionHelper(mContext: Context?) {

    val context = mContext
    val DB = SampleDBOpenHelper(context)
    val ingredient = DBContract.Ingredient
    val food_ingredient = DBContract.Foods_Ingredients
    val food = DBContract.Food
    val record = DBContract.Record

    // 品目名と、材料をそれぞれ　
    // SUGAR, FAT, PROTEIN, VITAMIN, MINERAL, FIBER, CALORIE, QUANTITY
    // の順番のフィールドを持つ辞書リスト渡すことで栄養クラスに変換する
    // ぶっちゃけ自分用な上引数も適当、例外チェックもガバガバなのでprivateにしときます。
    // 他でも使いたいとかあればきちんと作り直します。
    private fun getNutrition(foodname: String, dic: List<Dictionary>, foodID: Int): Nutrition? {

        //渡されたフィールドが正しいかチェック
        val field = ingredient.FIELD
        for (i in 0 until 7) {
            if (dic[i].field != "${ingredient.TABLE_NAME}.${field[i + 2]}") {
                println("no")
                return null
            }
        }

        val quantity = dic[7].toFloat().data   //品目材料テーブルの数量
        // 後は計算した品目ごとの栄養分
        val sugar = dic[0].toFloat().sum(quantity)
        val fat = dic[1].toFloat().sum(quantity)
        val protein = dic[2].toFloat().sum(quantity)
        val vitamin = dic[3].toFloat().sum(quantity)
        val mineral = dic[4].toFloat().sum(quantity)
        val fiber = dic[5].toFloat().sum(quantity)
        val calorie = dic[6].toFloat().sum(quantity)

        return Nutrition(foodname, sugar, fat, protein, vitamin, mineral, fiber, calorie, foodID)
    }

    // 品目IDのリストを受け取り、対応した品目名と栄養分を返す関数
    // もっと別の引数がいいとかあると思います。僕もそう思います。
    // ただ、手間なので、希望があったときに言ってもらえると助かります。
    // 自分は自分が欲しい時しか加筆しないです。
    fun getNutritions(ID: List<Int>): List<Nutrition>? {
        println("Nut check: start")

        if(ID.isEmpty()){
            return mutableListOf<Nutrition>()
        }

        // 抽出するカラム
        // ここでは計算に使うものだけ
        val column = arrayOf(
            "${ingredient.TABLE_NAME}.${ingredient.SUGAR}",
            "${ingredient.TABLE_NAME}.${ingredient.FAT}",
            "${ingredient.TABLE_NAME}.${ingredient.PROTEIN}",
            "${ingredient.TABLE_NAME}.${ingredient.VITAMIN}",
            "${ingredient.TABLE_NAME}.${ingredient.MINERAL}",
            "${ingredient.TABLE_NAME}.${ingredient.FIBER}",
            "${ingredient.TABLE_NAME}.${ingredient.CALORIE}",
            "${food_ingredient.TABLE_NAME}.${food_ingredient.NUMBER}"
        )

        // where句の作成
        // もっとスマートにいかんかねぇ
        var conditionSql: String = ""
        val selectionArgs: Array<String> = Array(ID.size) { ID[it].toString() }
        var head = true
        ID.forEach {
            if (head) {
                conditionSql += "${food_ingredient.TABLE_NAME}.${food_ingredient.FOOD_ID} = ?"
                head = false
            } else {
                conditionSql += " OR ${food_ingredient.TABLE_NAME}.${food_ingredient.FOOD_ID} = ?"
            }
        }

        // 栄養分の検索の実行
        val result = DB.searchRecord_dic(
            food_ingredient.TABLE_NAME, column, conditionSql, selectionArgs,
            innerJoin = Join(ingredient.TABLE_NAME, food_ingredient.INGREDIENT_ID, ingredient.ID)
        ) ?: return null

//        result[0].data.forEach {
//            println(it)
//        }

        // グループ情報、これはマジで別にしたほうがいいです。ややこしくなります。お気を付けを
        // 品目名やIDなどそれぞれの識別に使うデータはこっちで抽出している。
        val ident = DB.searchRecord_dic(
            food_ingredient.TABLE_NAME,
            arrayOf("${food_ingredient.TABLE_NAME}.${food_ingredient.FOOD_ID}", "${food.TABLE_NAME}.${food.NAME}"),
            conditionSql, selectionArgs,
            group = "${food_ingredient.TABLE_NAME}.${food_ingredient.FOOD_ID}",
            innerJoin = Join(food.TABLE_NAME, food_ingredient.FOOD_ID, food.ID)
        ) ?: return null


        val nutritions = mutableListOf<Nutrition>()  //最終的に返す栄養クラスのリスト
        val idList = ident[0].toInt().data    //使いやすいようidの辞書をList<Int>に
        val nameList = ident[1].toStr().data  //使いやすいようnameの辞書をList<String>に
        val groupNum = ident[2].toInt().data  //使いやすいようグループカウントの辞書をList<Int>に
        var post = 0                          //リストの位置、ポジションを保持するため
        var begin = 0                         //for文のループ開始位置の記憶
        var end = 0                           //for文のループ終了位置の記憶

        // まだ使ってないけど拡張のしやすさ考えてforでなくidで管理してループ回してます。
        idList.forEach {
            // grouNumの持つ、品目が持つ材料の数を参考に、
            // DIcのリストであるリザルト、そのリザルトにあるそれぞれの辞書の持つデータから
            // groupNum[post]個抜き取って、それらを使って品目の栄養分を求める。
            val dicList = mutableListOf<Dictionary>()  //栄養計算用の辞書リスト

            end += groupNum[post]
            result.forEach {
                val dic = Dictionary(mutableListOf(), it.field)
                for (i in begin until end) {
                    dic.data.add(it.data[i])
                }
                dicList.add(dic)   //getNutritioinに渡す辞書リストの作成
            }
            begin += groupNum[post]

            // println("post$post : ${nameList[post]}, idList=${idList[post]}, data=${dicList[0].data}")

            val nutrition = getNutrition(nameList[post], dicList, idList[post]) ?: return null   //栄養クラスの作成

            nutritions.add(nutrition)   //栄養クラスのリスト。1品目にひとつある。
            post++  //一歩進む
        }

        Log.d("getNutrition", "success")
        return nutritions
    }
    // getNutritions終わり

    private fun necessaryNut(period: Int): Nutrition?{
        val user = DB.searchRecord(DBContract.UserInfo.TABLE_NAME) ?: return null

        val age = user[4].toInt()
        val sex = user[5].toInt()
        val height = user[2].toFloat()
        val weight = user[3].toFloat()
        var cal = 0f
        var fiber = 20f
        var vitamin = 0f
        var mineral = 0f

        // Harris-Benedict式
        // 幼年時の計算は別途に必要と思われる
        if (sex == 0) {
            cal = 66f + (13.7f * weight) + (5.0f * height) - (6.8f * age)
            fiber = 20f
        } else if (sex == 1) {
            cal = 655f + (9.6f * weight) + (1.7f * height) - (4.7f * age)
            fiber = 18f
        }
        var protein = weight * 0.8f
        var fat = cal / 36f
        var sugar = cal * 0.15f

        return Nutrition("necessary", sugar*period, fat*period, protein*period,
            vitamin*period, mineral*period, fiber*period, cal*period)
    }

    // ユーザーの身体情報とこれまでの食事から
    // 必要な栄養バランスを求める関数。
    // まだ計算方法分からなくて未実装です。ごめんなさい。
    private fun getLackNut(nutritions: List<Nutrition>, period: Int): Nutrition? {

        // ユーザー情報と機関から最適な栄養バランスを求める
        val necNut = necessaryNut(period) ?: return null

        var calSum = 0f
        var sugarSum = 0f
        var fatSum = 0f
        var proteinSum = 0f
        var vitaminSum = 0f
        var mineralSum = 0f
        var fiberSum = 0f
        // 食事記録からこれまで摂取した栄養の合計を求める
        nutritions.forEach {
            sugarSum += it.sugar
            fatSum += it.fat
            proteinSum += it.protein
            vitaminSum += it.vitamin
            mineralSum += it.mineral
            fiberSum += it.fiber
            calSum += it.calorie
        }

        println("ATE : $sugarSum, $fatSum, $proteinSum, $fiberSum, $calSum")

        var sugar = necNut.sugar - sugarSum
        var fat = necNut.fat - fatSum
        var protein = necNut.protein - proteinSum
        var fiber = necNut.fiber - fiberSum
        var cal = necNut.calorie - calSum

        if (sugar <= 0) sugar = 0f
        if (fat <= 0) fat = 0f
        if (protein <= 0) protein = 0f
        if (fiber <= 0) fiber = 0f
        if (cal <= 0) cal = 0f

        println("LACK : $sugar, $fat, $protein, $fiber, $cal")

        return Nutrition(
            "", sugar, fat, protein, 0f, 0f,
            fiber, cal
        )
    }

    private fun scoreCalc(nutritions: List<Nutrition>, period: Int): Float? {
        // ユーザー情報と機関から最適な栄養バランスを求める
        val necNut = necessaryNut(period) ?: return null

        var calSum = 0f
        var sugarSum = 0f
        var fatSum = 0f
        var proteinSum = 0f
        var vitaminSum = 0f
        var mineralSum = 0f
        var fiberSum = 0f
        // 食事記録からこれまで摂取した栄養の合計を求める
        nutritions.forEach {
            sugarSum += it.sugar
            fatSum += it.fat
            proteinSum += it.protein
            vitaminSum += it.vitamin
            mineralSum += it.mineral
            fiberSum += it.fiber
            calSum += it.calorie
        }

        // それぞれの栄養分の過不足の割合
        var sugarPer = sugarSum/necNut.sugar
        if(sugarPer > 1) sugarPer = 1-(sugarPer-1) // 取りすぎ分を減点

        var fatPer = fatSum/necNut.fat
        if(fatPer > 1) fatPer = 1-(fatPer-1) // 取りすぎ分を減点

        var proteinPer = proteinSum/necNut.protein
        if(proteinPer > 1) proteinPer = 1-(proteinPer-1) // 取りすぎ分を減点

        var fiberPer = fiberSum/necNut.fiber
        if(fiberPer > 1) fiberPer = 1f // 上限以降は考えない

        var calPer = calSum/necNut.calorie
        if(calPer > 1) calPer = 1-(calPer-1) // 取りすぎ分を減点

        var resultScore = (sugarPer + fatPer + proteinPer + fiberPer + calPer)/5 * 100 + 10

        if(resultScore > 100) resultScore = 100f
        else if(resultScore < 0) resultScore = 0f

        return resultScore
    }

    // 一番足りてない栄養素を計算・選択する。
    // 拡張予定あり
    fun selectLack(baseNut: Nutrition): String {

        var distant = 0.2f
        var lackNut: String = ""

        val base = baseNut.normalization()

        if (distant < base.sugar) {
            lackNut = ingredient.SUGAR
            distant = base.sugar
        }
        if (distant < base.fat) {
            lackNut = ingredient.FAT
            distant = base.fat
        }
        if (distant < base.protein) {
            lackNut = ingredient.PROTEIN
            distant = base.protein
        }
        if (distant < base.vitamin) {
            lackNut = ingredient.VITAMIN
            distant = base.vitamin
        }
        if (distant < base.mineral) {
            lackNut = ingredient.MINERAL
            distant = base.mineral
        }
        if (distant < base.fiber) {
            lackNut = ingredient.FIBER
            distant = base.fiber
        }

        return lackNut
    }

    // ベクトル空間をもとにbaseNutに最も近い栄養を
    // 正規化した栄養リストの中から一つ選択して返す関数。
    // そのうち、抽出するクラスの数を設定できるようにするとよい
    private fun vectorsearch(baseNut: Nutrition): List<Nutrition>? {
        println("vectorSearch start")
        val nut_dist = mutableListOf<Pair<Nutrition, Float>>()
        val base = baseNut.normalization()
        var tmpDist = 1f

        val foodList = DB.searchRecord_dic(food.TABLE_NAME, arrayOf(food.ID)) ?: return null
        val foodIDList = foodList[0].toInt()

        val nutritions = getNutritions(foodIDList.data) ?: return null

        var result = nutritions[0]

        nutritions.forEach {
            val tmp = it.normalization() // 正規化のために一時仕様

            // それぞれの要素の差を求める
            val sugar = tmp.sugar - base.sugar
            val fat = tmp.fat - base.fat
            val protein = tmp.protein - base.protein
            val vitamin = tmp.vitamin - base.vitamin
            val mineral = tmp.mineral - base.mineral
            val fiber = tmp.fiber - base.fiber

            // 求められた差から必要な栄養分とのベクトル空間上の距離を求める。
            val dist = sqrt(
                sugar * sugar + fat * fat + protein * protein + vitamin * vitamin
                        + mineral * mineral + fiber * fiber
            )

            // 距離が更新できるなら返り値を対応する栄養クラスに更新する
            println("${result.foodname}:$tmpDist VS ${it.foodname}:$dist")
            if (dist < tmpDist) {
                tmpDist = dist
                result = it
            }

            nut_dist.add(Pair(it, dist))
            println("winner : ${result.foodname}")
        }

        nut_dist.sortBy { it.second }

        val nutList = mutableListOf<Nutrition>()
        nut_dist.forEach {
            nutList.add(it.first)
        }

        // 繰り返しの中で最もbaseNutに近い要素を持つ栄養クラスを返す。
        return nutList
    }

    private class calenderSQL(val condition: String, val selectionArgs: Array<String>, val period: Int)

    private fun makeCalenderCondition(year: Int, month: Int, day: Int, span: Int): calenderSQL?{
        var conditionSql = ""
        var selectionArgs = arrayOf("")
        var period = 0

        // 日別の場合
        if (span == 0) {
            conditionSql += "${record.TABLE_NAME}.${record.YEAR} = ? AND " +
                    "${record.TABLE_NAME}.${record.MONTH} = ? AND " +
                    "${record.TABLE_NAME}.${record.DATE} = ?"
            selectionArgs = arrayOf(year.toString(), month.toString(), day.toString())
            period = 1
        }

        // 週別の場合
        else if (span == 1) {
            conditionSql += "${record.TABLE_NAME}.${record.YEAR} = ? " +
                    "AND ${record.TABLE_NAME}.${record.MONTH} = ? " +
                    "AND ${record.TABLE_NAME}.${record.DATE} >= ? " +
                    "AND ${record.TABLE_NAME}.${record.DATE} <= ?"
            selectionArgs = arrayOf(year.toString(), month.toString())
            if (day < 8) {
                selectionArgs = arrayOf(year.toString(), month.toString(), "1", "7")
                period = 7
            } else if (day < 15) {
                selectionArgs = arrayOf(year.toString(), month.toString(), "8", "14")
                period = 7
            } else if (day < 22) {
                selectionArgs = arrayOf(year.toString(), month.toString(), "15", "21")
                period = 7
            } else if (day < 29) {
                selectionArgs = arrayOf(year.toString(), month.toString(), "22", "28")
                period = 7
            } else {
                selectionArgs = arrayOf(year.toString(), month.toString(), "29", "31")
                if (month == 2) {
                    if (year % 4 == 0) {
                        period = 1
                    } else {
                        period = 0
                    }
                } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                    period = 2
                } else {
                    period = 3
                }
            }
        }

        // 月別の場合
        else if (span == 2) {
            conditionSql += "${record.TABLE_NAME}.${record.YEAR} = ? " +
                    "AND ${record.TABLE_NAME}.${record.MONTH} = ?"
            selectionArgs = arrayOf(year.toString(), month.toString())
            if (month == 2) {
                if (year % 4 == 0) {
                    period = 29
                } else {
                    period = 28
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                period = 30
            } else {
                period = 31
            }
        } else {
            println("invalid span")
            println("日別:span=0、週別:span=1、月別:span=2")
            return null
        }

        return calenderSQL(conditionSql, selectionArgs, period)
    }

    fun recordScore(year: Int, month: Int, day: Int, span: Int): Float? {
        // 与えられた情報から日付に関する条件句を作成
        val sqlElement = makeCalenderCondition(year, month, day, span) ?: return null
        val conditionSql = sqlElement.condition
        val selectionArgs = sqlElement.selectionArgs
        val period = sqlElement.period

        val foodDicList = DB.searchRecord_dic(
            record.TABLE_NAME, arrayOf("${food.TABLE_NAME}.${food.ID}"),
            condition = conditionSql, selectionArgs = selectionArgs,
            innerJoin = Join(food.TABLE_NAME, record.FOOD_ID, food.ID)
        ) ?: return null

        val foodIDList = foodDicList[0].toInt().data

        // レコードが空の場合は0点
        if(foodIDList.isEmpty()){
            return 0f
        }

        val foodNutList = getNutritions(foodIDList) ?: return null

        //足りてない栄養を計算
        val result = scoreCalc(foodNutList, period) ?: return null

        return result
    }

    // おすすめ品目選択を行うクラス。
    // 食事記録のリストと、計測する期間(日数)を渡して
    // 全品目の類似度順にリザルトクラスで返す。
    // spanは計測期間を分けるもの
    // 日別:span=0、週別:span=1、月別:span=2
    fun selectFood(year: Int, month: Int, day: Int, span: Int): Result? {

        // 与えられた情報から日付に関する条件句を作成
        val sqlElement = makeCalenderCondition(year, month, day, span) ?: return null
        val conditionSql = sqlElement.condition
        val selectionArgs = sqlElement.selectionArgs
        val period = sqlElement.period

        val foodDicList = DB.searchRecord_dic(
            record.TABLE_NAME, arrayOf("${food.TABLE_NAME}.${food.ID}"),
            condition = conditionSql, selectionArgs = selectionArgs,
            innerJoin = Join(food.TABLE_NAME, record.FOOD_ID, food.ID)
        ) ?: return null

        val foodIDList = foodDicList[0].toInt().data
        val foodNutList = getNutritions(foodIDList) ?: return null

        //足りてない栄養を計算
        val baseNut = getLackNut(foodNutList, period) ?: return null

        // 足りてない栄養からそれらを補いうる品目を求める
        val nutList = vectorsearch(baseNut) ?: return null
        val result = Result(mutableListOf())

        nutList.forEach {
            it.foodID ?: return null
            result.foodIDList.add(it.foodID)
        }
        // 最適な品目のIDを返す
        return result
    }
}

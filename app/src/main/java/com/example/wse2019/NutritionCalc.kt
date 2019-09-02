package com.example.wse2019

import android.content.Context
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper

// 栄養分析に用いるクラス。品目名とその材料から算出した栄養分の総量を引数にもっています。
class Nutrition(
    val foodname: String, val sugar: Float, val fat: Float, val protein: Float,
    val vitamin: Float, val mineral: Float, val fiber: Float, val calorie: Float
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
}

// 栄養分の計算や分析を担当するクラスNutritionHelperです。
class NutritionHelper(mContext: Context?) {

    val context = mContext
    val DB = SampleDBOpenHelper(context)
    val ingredient = DBContract.Ingredient
    val food_ingredient = DBContract.Foods_Ingredients
    val food = DBContract.Food

    // 単純に品目名を受け取り、品目の栄養分を返す関数
    fun getNutrition(foodName: String): Nutrition? {
        println("Nut check: start")

        // 抽出するカラム
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

        // where句
        val conditionSql: String = "${food.TABLE_NAME}.${food.NAME} = ?"

        // 内部結合の設定
        val multiJoin = arrayOf(
            Join(food_ingredient.TABLE_NAME, food.ID, food_ingredient.FOOD_ID),
            Join(ingredient.TABLE_NAME, food_ingredient.INGREDIENT_ID, ingredient.ID, food_ingredient.TABLE_NAME)
        )

        // 検索の実行
        val result = DB.searchRecord_dic(
            food.TABLE_NAME, column, conditionSql, arrayOf(foodName),
            multiJoin = multiJoin
        ) ?: return null

        val quantity = result[7].toInt().data   //品目材料テーブルの数量
        // 後は計算した品目ごとの栄養分
        val sugar = result[0].toFloat().sum(quantity)
        val fat = result[1].toFloat().sum(quantity)
        val protein = result[2].toFloat().sum(quantity)
        val vitamin = result[3].toFloat().sum(quantity)
        val mineral = result[4].toFloat().sum(quantity)
        val fiber = result[5].toFloat().sum(quantity)
        val calorie = result[6].toFloat().sum(quantity)

        return Nutrition(foodName, sugar, fat, protein, vitamin, mineral, fiber, calorie)
    }

}
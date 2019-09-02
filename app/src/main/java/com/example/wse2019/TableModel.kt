package com.example.sample

import android.content.Context
//import com.example.wse2019.Nutrition

sealed class Table() {
    val dic = mutableListOf<Dictionary>()

    class Ingredient(
        val name: String, val sugar: Float?, val fat: Float?, val protein : Float?,
        val vitamin: Float?, val mineral: Float?, val fiber: Float?, val calorie: Float?,
        val quantity : Float?, val unit: String?, val allergen: Int?, val clas: Int?
    ) : Table()

    class Food(val name: String, val favorite: Int?, val memo: String?, val category : Int?) : Table()

    class Record(val food_id: Int, val year: Int, val month: Int, val date: Int, val time: Int, val number: Float) : Table()

    class MyCondate(val name: String) : Table()

    class Category(val name: String?, val higher_id: Int? ) : Table()

    class Food_Ingredient(val food_id: Int, val Ingredient_id: Int, val num: Float) : Table()

    class MyCondate_Food(val MyCondate_id: Int, val food_id: Int, val num: Int) : Table()

    class UserInfo(val name: String, val height: Float?, val weight: Float?, val age: Int?, val sex: Int?) : Table()
}

fun selectTable(tableName: String): DBContract.KBaseColumns {
    val table =
        when(tableName){
            DBContract.Ingredient.TABLE_NAME -> DBContract.Ingredient
            DBContract.Food.TABLE_NAME -> DBContract.Food
            DBContract.Record.TABLE_NAME -> DBContract.Record
            DBContract.MyCondate.TABLE_NAME -> DBContract.MyCondate
            DBContract.Category.TABLE_NAME -> DBContract.Category
            DBContract.Foods_Ingredients.TABLE_NAME -> DBContract.Foods_Ingredients
            DBContract.MyCondate_Foods.TABLE_NAME -> DBContract.MyCondate_Foods
            DBContract.UserInfo.TABLE_NAME -> DBContract.UserInfo
            else -> DBContract.Ingredient
        }
    return table
}

//
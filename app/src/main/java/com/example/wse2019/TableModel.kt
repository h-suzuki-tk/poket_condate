package com.example.sample


sealed class Table {

    class Ingredient(
        val name: String, val sugar: Float?, val fat: Float?, val protein : Float?,
        val vitamin: Float?, val mineral: Float?, val fiber: Float?, val calorie: Float?,
        val quantity : Float?, val unit: String?, val allergen: Int?
    ) : Table()

    class Food(val name: String, val favorite: Int?, val memo: String?, val category : Int?) : Table()

    class Record(val food_id: Int, val year: Int, val month: Int, val date: Int, val time: Int) : Table()

    class MyCondate(val name: String) : Table()

    class Category(val name: String, val higher_id: Int?) : Table()

    class Food_Ingredient(val food_id: Int, val Ingredient_id: Int, val num: Int) : Table()

    class MyCondate_Food(val MyCondate_id: Int, val food_id: Int, val num: Int) : Table()

    class UserInfo(val name: String, val height: Float?, val weight: Float?, val age: Int?, val sex: Int?) : Table()

}
//
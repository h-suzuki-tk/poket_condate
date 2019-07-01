package com.example.wse2019

class Ingredient(val name: String, val sugar: Float, val fat: Float, val protein: Float,
                 val vitamin: Float, val mineral: Float, val fiber: Float, val calorie: Float,
                 val unit:Float, val allergen: Int)

class Food(val name: String, val favorite: Int, val memo: String)

class Record(val food_id: Int, val year: Int, val month: Int, val date: Int, val time: Int)

class MyCondate(val name:String)

class Category(val name: String, val higher_id: Int)

class Food_Ingredient(val food_id: Int, val Ingredient_id: Int, val num: Int)

class MyCondate_Food(val MyCondate_id: Int, val food_id: Int, val num: Int)

class UserInfo(val name:String, val height: Float, val weight: Float, val age: Int, val sex: Int)

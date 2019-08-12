package com.example.sample

import android.provider.BaseColumns

open class KBaseColumns(val TABLE_NAME : String){
    //ここでDB機能呼び出さるようにする
}

class DBContract {
    data class Column(val column:String, val type:String)

    enum class Type(val type:String){
        INT("Integer"),
        REAL("real"),
        TEXT("text"),
    }

    //材料テーブル
    class Ingredient : BaseColumns{
        companion object : KBaseColumns("ingredients") {
            const val ID = "id"                 //材料ID
            const val NAME = "name"             //材料名
            const val SUGAR = "sugar"           //糖質
            const val FAT = "fat"               //脂質
            const val PROTEIN = "protein"       //タンパク質
            const val VITAMIN = "vitamin"       //ビタミン
            const val MINERAL = "mineral"       //ミネラル
            const val FIBER = "fiber"           //繊維
            const val CALORIE = "calorie"       //カロリー
            const val QUANTITY = "quantity"     //量
            const val UNIT = "unit"             //単位
            const val ALLERGEN = "allergen"     //アレルギー
        }
    }

    //品目テーブル
    class Food : BaseColumns {
        companion object : KBaseColumns("foods"){  //テーブル名
            const val ID = "id"             //品目ID
            const val NAME = "name"         //品目名
            const val FAVORITE = "favorite" //お気に入り
            const val MEMO = "memo"         //メモ
            const val CATEGORY = "category"
        }
    }

    //食事記録テーブル
    class Record : BaseColumns {
        companion object : KBaseColumns("records"){ //テーブル名
            const val ID = "id"             //食事記録ID
            const val FOOD_ID = "food_id"   //品目ID
            const val YEAR = "year"  //年
            const val MONTH = "month"//月
            const val DATE = "date"  //日にち
            const val TIME = "time"  //時間帯
            const val NUMBER = "number" //何人前
        }
    }

    //My献立テーブル
    class MyCondate : BaseColumns {
        companion object : KBaseColumns("myCondate") {  //テーブル名
            const val ID = "id"             //My献立I
            const val NAME = "name"         //My献立名
        }
    }

    //分類テーブル
    class Category : BaseColumns {
        companion object : KBaseColumns("category"){    //テーブル名
            const val ID = "id"                 //分類ID
            const val NAME = "name"             //分類名
            const val HIGHER_ID = "higher_id"   //親カテゴリ
        }
    }


    //品目材料テーブル
    class Foods_Ingredients : BaseColumns {
        companion object : KBaseColumns("foods_ingredients"){ //テーブル名
            const val FOOD_ID = "food_id"               //品目ID
            const val INGREDIENT_ID = "ingredient_id"   //材料ID
            const val NUMBER = "number"                 //数量
        }
    }

    //献立内容テーブル
    class MyCondate_Foods : BaseColumns {
        companion object : KBaseColumns("myCondate_Foods"){
            const val MYCONDATE_ID = "myCondate_id"       //My献立ID
            const val FOOD_ID = "food_id"           //品目ID
            const val NUMBER = "number"             //何人前
        }
    }

    //ユーザー情報テーブル
    class UserInfo : BaseColumns {
        companion object : KBaseColumns("user_info"){
            const val ID = "id"                         //ユーザーID
            const val NAME = "name"                     //ユーザー名
            const val HEIGHT = "height"                 //身長
            const val WEIGHT = "weight"                 //体重
            const val AGE = "age"                       //年齢
            const val SEX = "sex"                       //性別
        }
    }
}
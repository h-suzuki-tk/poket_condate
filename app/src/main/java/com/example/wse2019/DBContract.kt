package com.example.wse2019

import android.provider.BaseColumns

class DBContract {
    data class Column(val column:String, val type:String)

    enum class Type(val type:String){
        INT("Integer"),
        TEXT("text"),
        REAL("real"),
    }

    //材料テーブル
    class Ingredient : BaseColumns {
        companion object {
            const val TABLE_NAME = "ingredients"//テーブル名
            const val ID = "id"                 //材料ID
            const val NAME = "name"             //材料名
            const val SUGAR = "sugar"           //糖質
            const val FAT = "fat"               //脂質
            const val PROTEIN = "protein"       //タンパク質
            const val VITAMIN = "vitamin"       //ビタミン
            const val MINERAL = "mineral"       //ミネラル
            const val FIBER = "fiber"           //繊維
            const val CALORIE = "calorie"       //カロリー
            const val UNIT = "unit"             //単位
            const val ALLERGEN = "allergen"     //アレルギー
        }
    }

    //品目テーブル
    class Food : BaseColumns {
        companion object {
            const val TABLE_NAME = "foods"   //テーブル名
            const val ID = "id"             //品目ID
            const val NAME = "name"         //品目名
            const val FAVORITE = "favorite" //お気に入り
            const val MEMO = "memo"         //メモ
        }
    }

    //食事記録テーブル
    class Record : BaseColumns {
        companion object {
            const val TABLE_NAME = "records" //テーブル名
            const val ID = "id"             //食事記録ID
            const val FOOD_ID = "food_id"   //品目ID
            const val YEAR = "year"  //年
            const val MONTH = "month"//月
            const val DATE = "date"  //日にち
            const val TIME = "time"  //時間帯
        }
    }

    //My献立テーブル
    class MyCondate : BaseColumns {
        companion object {
            const val TABLE_NAME = "myCondate" //テーブル名
            const val ID = "id"             //My献立ID
            const val NAME = "name"         //My献立名
        }
    }

    //分類テーブル
    class Category : BaseColumns {
        companion object {
            const val TABLE_NAME = "category"   //テーブル名
            const val ID = "id"                 //分類ID
            const val NAME = "name"             //分類名
            const val HIGHER_ID = "higher_id"   //親カテゴリ
        }
    }


    //品目材料テーブル
    class Foods_Ingredients : BaseColumns {
        companion object {
            const val TABLE_NAME = "foods_ingredients"  //テーブル名
            const val INGREDIENT_ID = "ingredient_id"   //材料ID
            const val FOOD_ID = "food_id"               //品目ID
            const val NUMBER = "number"                 //数量
        }
    }

    //献立内容テーブル
    class MyCondate_Foods : BaseColumns {
        companion object {
            const val TABLE_NAME = "myMenu_Foods"   //テーブル名
            const val MYMENU_ID = "myMenu_id"       //My献立ID
            const val FOOD_ID = "food_id"           //品目ID
            const val NUMBER = "number"             //何人前
        }
    }

    //ユーザー情報テーブル
    class UserInfo : BaseColumns {
        companion object {
            const val TABLE_NAME = "user_info"          //テーブル名
            const val ID = "id"                         //ユーザーID
            const val NAME = "name"                     //ユーザー名
            const val HEIGHT = "height"                 //身長
            const val WEIGHT = "weight"                 //体重
            const val AGE = "age"                       //年齢
            const val SEX = "sex"                       //性別
        }
    }
}
package com.example.wse2019

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import java.io.InputStream

const val FOOD_IMAGE_DIRECTORY = "foods"
const val NO_IMAGE_FILENAME = "0.png"

class FoodManager {
    private val imageManager = ImageManager()

    /*
     * @品目画像ファイル名 -> 品目ID.png
     * @比率 -> 4:3
     */
    fun getBitmap(context: Context, foodId: Int): Bitmap {
        return imageManager.getBitmap(context,
            directory   = FOOD_IMAGE_DIRECTORY,
            fileName    = "${foodId}.png"
        ) ?: imageManager.getBitmap(context,
            directory   = FOOD_IMAGE_DIRECTORY,
            fileName    = NO_IMAGE_FILENAME
        ) ?: throw NullPointerException("getBitmap was failed.")
    }


}
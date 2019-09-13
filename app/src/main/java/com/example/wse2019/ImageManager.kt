package com.example.wse2019

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import java.io.InputStream

// --------------------------------------------------
//  注意事項
// --------------------------------------------------
/*
 * @画像ファイルの置き場所  -> app/src/main/assets/任意のディレクトリ/
 * @表示方法(例:ImageView)  -> <ImageView>.setImageBitmap(getBitmap(...))
 */
// --------------------------------------------------
class ImageManager {

    /*
     * @params ->
     *      directory   : assetsディレクトリ直下に置いたディレクトリ
     *      fileName    : 拡張子含む
     */
    fun getBitmap(context: Context, directory: String, fileName: String): Bitmap? {

        var bitmap: Bitmap? = null

        try {
            val assetManager: AssetManager = context.resources.assets
            val inputStream: InputStream = assetManager.open("${directory}/${fileName}")
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e("Error", e.toString())
        }

        return bitmap
    }

}
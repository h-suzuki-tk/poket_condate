package com.example.wse2019

import android.content.*
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import java.io.*
import android.util.Log


//csvを1行ずつ読み取り、splitしてInsertする関数
/*csvファイルの行は　名前,炭水化物,脂質,タンパク質,ビタミン,ミネラル,カロリー,量,単位,アレルゲン(アレルギーが出ない食べ物は一説によるとないらしいのでいまはすべて1にしている),区分
  という形で記述する  また、csvファイルはShift-JISにすること(文字化けするため)*/
    fun csvImportHelper(context: Context,db:SampleDBOpenHelper,filename:String) {
        val assetManager = context.getResources().getAssets()
        try {
            // CSVファイルの読み込み

            val inputStream = assetManager.open(filename)
            val inputStreamReader = InputStreamReader(inputStream,"SJIS")
            val bufferReader = BufferedReader(inputStreamReader)
            var line: String?
            do{

                line = bufferReader.readLine()
                if(line==null){
                    break
                }

                //カンマ区切りで１つづつ配列に入れる
                Log.d("line:",line)
                var RowData = line.split(",")

                var i=0

                var data=ArrayList<String>()

                while(i<RowData.size){
                    if(RowData[i]=="Tr" || RowData[i]=="-" || Regex("""\(.+\)""").matches(RowData[i])){
                        data.add("0")
                    }else{
                        data.add(RowData[i])
                    }
                    i++
                }

                db.insertRecord(
                    Table.Ingredient(
                        data[0].toString(), (data[1].toFloat()-data[6].toFloat())/100f, data[2].toFloat()/100f, data[3].toFloat()/100f, null, 0f,
                        data[6].toFloat()/100f, data[7].toFloat()/100f, data[8].toFloat(), data[9].toString(),1, data[11].toInt()
                    )
                )



            }while(true)
            bufferReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
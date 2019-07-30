package com.example.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*

class DBViewer : AppCompatActivity() {

    //保存されている文字列をリスト表示するメソッド
    //配列化はまだできてないです。チャレンジはしてみます。
    private fun show(tablename : String) {
        val elements = SampleDBOpenHelper(this).searchRecord(tablename, arrayOf("*"))

        val num : Int = columnNum(tablename)
        var count = 0
        var row = ""
        var rows = mutableListOf<String>()
        elements.forEach {
            count++
            row += "| $it "
            if (count == num) {
                row += "|"
                rows.add(row)
                row = ""
                count = 0
            }
        }

        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = ArrayAdapter<String>(
            this,
            R.layout.list_text_row, R.id.textView, rows
        )
    }

    //プルダウンメニューの選択肢
    val spinnerItems = arrayOf(
        DBContract.Ingredient.TABLE_NAME,
        DBContract.Food.TABLE_NAME,
        DBContract.Record.TABLE_NAME,
        DBContract.MyCondate.TABLE_NAME,
        DBContract.Category.TABLE_NAME,
        DBContract.Foods_Ingredients.TABLE_NAME,
        DBContract.MyCondate_Foods.TABLE_NAME,
        DBContract.UserInfo.TABLE_NAME
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("program check", "program start")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //今のところ、データを作り直して仮のデータを入れるまで実行
        //test1(this)
        initializer(this)

        Log.d("program check", "program start2")
        //ここから、閲覧するテーブルを決定するプルダウンメニューの設定
        val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            //メニューでテーブルが選択された時の処理
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //選択したテーブル名を取得し、実際にビュワーをつかさどるshowに渡す
                val spinnerParent = parent as Spinner
                val tableName = spinnerParent.selectedItem as String
                show(tableName)
            }
            //テーブルが選択されていないときの処理
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //何もしない
            }
        }
    }
}

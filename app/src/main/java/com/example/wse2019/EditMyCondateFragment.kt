package com.example.wse2019

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import org.w3c.dom.Text

private const val ADD="+"


class EditMyCondateFragment(): Fragment(){

    private var itemTextView=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //渡した値の取り出しを行う
        val bundle=arguments
        if(bundle!=null){
            itemTextView=bundle.getString("MYCONDATE_NAME")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_edit_my_condate,container,false)


        val DB = SampleDBOpenHelper(requireContext())
        //リストビューを探す
        val myListView=v.findViewById<ListView>(R.id.listView1)
        val textCondateName=v.findViewById<TextView>(R.id.condate_name)
        val textCalorie=v.findViewById<TextView>(R.id.calorie)
        val textProtein=v.findViewById<TextView>(R.id.protein)
        val textVitamin=v.findViewById<TextView>(R.id.vitamin)
        val textFat=v.findViewById<TextView>(R.id.fat)
        val textSugar=v.findViewById<TextView>(R.id.sugar)
        val textMineral=v.findViewById<TextView>(R.id.mineral)
        textCondateName.setText(itemTextView)


        //合計値を計算し、値をセットする
        textCalorie.setText("")
        textProtein.setText("")
        textFat.setText("")
        textMineral.setText("")
        textSugar.setText("")
        textVitamin.setText("")

        var map= mutableMapOf<String,String>()

        val res=DB.searchRecord(DBContract.MyCondate.TABLE_NAME,
            arrayOf(DBContract.MyCondate.ID),
            condition = "${DBContract.MyCondate.NAME} = ?",
            selectionArgs = arrayOf(itemTextView)
        )?:TODO()


        //献立idの取得
        val myCondateId=res[0]

        //リストビューで表示
        var items=loadList(DB,map)

        val adapter =ArrayAdapter(context,android.R.layout.simple_list_item_1,items)
        myListView.adapter=adapter







        // 項目をタップしたときの処理
        myListView.setOnItemClickListener {parent,view, position, id ->

            var isValid=true

            val foodName=adapter.getItem(position)

            //画面遷移処理
            if(isValid){
                if(foodName==ADD){
                    //項目追加のための画面遷移処理


                }else{
                    //テキスト名をfragmentにセット
                    val bundle= Bundle()

                    bundle.putString("FOOD_ID",map[foodName])
                    val fragment =FoodInfFragment()
                    fragment.setArguments(bundle)

                    //fragmentの張替え
                    val fragmentManager = fragmentManager
                    val transaction = fragmentManager!!.beginTransaction()
                    transaction.replace(R.id.frame_contents,fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }

            adapter.notifyDataSetChanged()

        }

        // 項目を長押ししたときの処理
        myListView.setOnItemLongClickListener { parent, view, position, id ->

            val foodName=adapter.getItem(position)

            if(foodName==null){
                TODO()
            }

            //"+"であれば、何もしない
            if(foodName==ADD){
                return@setOnItemLongClickListener true
            }

            AlertDialog.Builder(requireContext()).apply {
                setTitle("${foodName}を削除しますか?")
                setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    // OKをタップしたときの処理、項目の削除

                    DB.deleteRecord(DBContract.MyCondate_Foods.TABLE_NAME,
                        "${DBContract.MyCondate_Foods.FOOD_ID} = ? and ${DBContract.MyCondate_Foods.MYCONDATE_ID} = ?",
                        arrayOf(map[foodName]!!,myCondateId))

                    adapter.clear()
                    items=loadList(DB,map)
                    adapter.addAll(items)

                    Toast.makeText(context, "${foodName}を${itemTextView}から削除しました。", Toast.LENGTH_LONG).show()
                })
                setNegativeButton("Cancel", null)
                show()
            }

            adapter.notifyDataSetChanged()

            return@setOnItemLongClickListener true
        }

        DB.close()

        return v

    }

    //itemをロードする関数
    private fun loadList(DB: SampleDBOpenHelper,map: MutableMap<String,String>):ArrayList<String>{
        //献立を配列に入れる

        val multiJoin = arrayOf(
            //献立品目テーブルを結合
            Join(DBContract.MyCondate_Foods.TABLE_NAME,
                DBContract.Food.ID,
                DBContract.MyCondate_Foods.FOOD_ID),
            //
            Join(DBContract.MyCondate.TABLE_NAME,
                DBContract.MyCondate_Foods.MYCONDATE_ID,
                DBContract.MyCondate.ID,
                DBContract.MyCondate_Foods.TABLE_NAME)
        )

        val columns = arrayOf(
            "${DBContract.Food.TABLE_NAME}.${DBContract.Food.ID}",
            "${DBContract.Food.TABLE_NAME}.${DBContract.Food.NAME}"
        )

        val result=DB.searchRecord(DBContract.Food.TABLE_NAME,
            columns,
            "${DBContract.MyCondate.TABLE_NAME}.${DBContract.MyCondate.NAME} = ?",
            arrayOf(itemTextView),
            multiJoin = multiJoin)?:TODO()

        val size=result.size
        val items=ArrayList<String>()



        var i=1
        while(i<size){
            items.add(result[i])
            i=i+2
        }
        //連想配列にidと名前の組み合わせを保存
        i=0
        while(i<size){
            map[result[i+1]]=result[i]
            i=i+2
        }
        //最後の項目に+を表示
        items.add(ADD)

        return items


    }

}
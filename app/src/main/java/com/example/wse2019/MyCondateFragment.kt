package com.example.wse2019


import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.sample.SampleDBOpenHelper
import android.widget.TextView
import com.example.sample.DBContract
import android.widget.Toast


class MyCondateFragment(): Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_my_condate, container,false)

        //献立を配列に入れる
        val DB = SampleDBOpenHelper(requireContext())
        val columns = arrayOf(DBContract.MyCondate.NAME)
        val result = DB.searchRecord(DBContract.MyCondate.TABLE_NAME, columns)
        if(result==null){
            //エラー処理
        }

        val size=result!!.size
        val items=Array<String>(size,{""})

        var i=0
        while(i<size){
            items[i]=result[i]
            i=i+1
        }
        //リストビューで表示
        val myListView=v.findViewById<ListView>(R.id.listView1)
        val adapter =ArrayAdapter<String>(this.context,android.R.layout.simple_list_item_1,items)
        myListView.adapter=adapter

        // 項目をタップしたときの処理
        myListView.setOnItemClickListener {parent, view, position, id ->

            var isValid=true

            // 項目の TextView を取得
            val itemTextView : TextView = view.findViewById(android.R.id.text1)

            // 項目のラベルテキストをログに表示
            Log.i("debug", itemTextView.text.toString())


            //画面遷移処理
            if(isValid){
                //テキスト名をfragmentにセット
                val bundle= Bundle()
                val condateName=adapter.getItem(position)
                bundle.putString("MYCONDATE_NAME",condateName)
                val fragment =EditMyCondateFragment()
                fragment.setArguments(bundle)

                //fragmentの張替え
                val fragmentManager = fragmentManager
                val transaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.frame_contents,fragment)
                transaction.commit()
            }






        }

        // 項目を長押ししたときの処理
        myListView.setOnItemLongClickListener { parent, view, position, id ->


            //長押しされたとき、項目を削除する
            val condateName=adapter.getItem(position)
            DB.deleteRecord(DBContract.MyCondate.TABLE_NAME,"${DBContract.MyCondate.NAME} = ?",arrayOf(condateName))

            val result = DB.searchRecord(DBContract.MyCondate.TABLE_NAME, columns)
            if(result==null){
                //エラー処理
            }

            val size=result!!.size
            val items=Array<String>(size,{i->""})

            var i=0
            while(i<size){
                items[i]=result[i]
                i=i+1
            }
            //リストビューで表示
            val myListView=v.findViewById<ListView>(R.id.listView1)
            val adapter =ArrayAdapter<String>(this.context,android.R.layout.simple_list_item_1,items)
            myListView.adapter=adapter
            adapter.notifyDataSetChanged()

            return@setOnItemLongClickListener true
        }

        return v

    }










}
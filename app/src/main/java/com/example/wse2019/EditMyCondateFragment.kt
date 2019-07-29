package com.example.wse2019

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView


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

        //テスト用　データ受け渡しの確認
        val textView=v.findViewById<TextView>(R.id.textView1)
        textView.setText(itemTextView)
        return v
    }
}
package com.example.wse2019

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class FinishEditInfFragment(): Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_finish_edit_inf, container, false)

        // ボタンを取得して、ClickListenerをセット
        val btn3 = v.findViewById(R.id.button3) as Button
        val btn4 = v.findViewById(R.id.button4) as Button
        //編集が押されたときの処理
        btn3.setOnClickListener{
            var isValid1=true

            if(isValid1){
                val fragmentManager = fragmentManager
                val transaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.frame_contents,RegistrationInfEditFragment())
                transaction.commit()
            }

        }

        //Topへが押されたときの処理
        btn4.setOnClickListener{
            var isValid2=true

            if(isValid2){
                val fragment=TabFragment()
                val arg = Bundle()
                arg.putInt("PageIndex", 0)
                fragment.arguments = arg
                val fragmentManager = fragmentManager
                val transaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.frame_contents,TabFragment())
                transaction.commit()
            }
        }

        return v
    }
}
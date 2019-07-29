package com.example.wse2019



import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import android.widget.Button


class RegistrationInfEditFragment(): Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v= inflater.inflate(R.layout.fragment_regisration_inf_edit, container,false)

        val name=v.findViewById<TextView>(R.id.name)
        val sex=v.findViewById<TextView>(R.id.sex)
        val age=v.findViewById<TextView>(R.id.age)
        val height=v.findViewById<TextView>(R.id.height)
        val weight=v.findViewById<TextView>(R.id.weight)

        //ユーザー情報を取り出す
        val DB= SampleDBOpenHelper(requireContext())

        val result = DB.searchRecord(DBContract.UserInfo.TABLE_NAME)
        if(result==null){
            //エラー処理

        }

        //ユーザー情報を代入する 今回はユーザー情報が1人分しかないという仮定のもと、作成
        name.setText(result?.get(1).toString())
        height.setText(result?.get(2).toString())
        weight.setText(result?.get(3).toString())
        age.setText(result?.get(4).toString())
        if(result?.get(5).toString()=="1"){
            sex.setText("男性")
        }else if(result?.get(5).toString()=="0"){
            sex.setText("女性")
        }else{
            //エラー処理
        }

        val btn=v.findViewById<Button>(R.id.button)


        //編集画面への画面遷移処理
        btn.setOnClickListener{
            var isValid2=true

            if(isValid2){
                val fragmentManager = fragmentManager
                val transaction = fragmentManager!!.beginTransaction()

                transaction.replace(R.id.frame_contents,EditInfFragment())
                transaction.commit()
            }
        }


        return v
    }
}
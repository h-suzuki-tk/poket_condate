package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.util.Log
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper


class EditInfFragment() : Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_edit_inf, container, false)

        // ボタンを取得して、ClickListenerをセット
        val btn1 = v.findViewById(R.id.button1) as Button
        val btn2 = v.findViewById(R.id.button2) as Button


        val nameEditText=v.findViewById<EditText>(R.id.name)
        val ageEditText=v.findViewById<EditText>(R.id.age)
        val sexRadioGroup=v.findViewById<RadioGroup>(R.id.sex)
        val heightEditText=v.findViewById<EditText>(R.id.height)
        val weightEditText=v.findViewById<EditText>(R.id.weight)


        //入力ボックスの初期値設定のため、search
        val DB=SampleDBOpenHelper(requireContext())
        val userInfo=DB.searchRecord(DBContract.UserInfo.TABLE_NAME)

        if(userInfo==null){
            //エラー処理
        }

        //入力ボックスの初期値設定.
        nameEditText.setText(userInfo?.get(1).toString())
        ageEditText.setText(userInfo?.get(4).toString())
        if(userInfo?.get(5).toString()=="1"){
            sexRadioGroup.check(R.id.male)
        }else if(userInfo?.get(5).toString()=="0"){
            sexRadioGroup.check(R.id.female)
        }else{
            //エラー処理
        }
        heightEditText.setText(userInfo?.get(2).toString())
        weightEditText.setText(userInfo?.get(3).toString())


        //編集が押されたときの処理
        btn1.setOnClickListener{
            var isValid1=true

            if(isValid1){
                //編集ボタンが押されたら、値をとってきてデータベースに入れる

                val name=nameEditText.text.toString()
                val age=ageEditText.text.toString()

                val RadioButtonId=sexRadioGroup.checkedRadioButtonId
                val radioButton=sexRadioGroup.findViewById<RadioButton>(RadioButtonId)
                val sexIndex=sexRadioGroup.indexOfChild(radioButton).toString()

                var sex=-1
                if(sexIndex=="男性"){
                    sex=1
                }else if(sexIndex=="女性"){
                    sex=0
                }

                //男性または女性の規定値が入っていなかったら、エラー処理
                if(sex!=0 || sex!=1){
                    //エラー処理
                }

                val height=heightEditText.text.toString()
                val weight=weightEditText.text.toString()


                //値が適切であるかを確認
                val regex = "^[0-9]*[¥.]?[0-9]+$"
                val regex2="[0-120]"
                val regHeight=height.toRegex()
                val regWeight=weight.toRegex()
                val regAge=age.toRegex()

                if(regex.matches(regWeight)==false || regex.matches(regHeight)==false || regex2.matches(regAge)==false){
                    //エラー処理
                }


                //update文の処理,変更する項目の配列と内容の配列をそれぞれ用意
                val str=arrayOf(DBContract.UserInfo.NAME,DBContract.UserInfo.HEIGHT,DBContract.UserInfo.WEIGHT,DBContract.UserInfo.AGE,DBContract.UserInfo.SEX)
                val str2=arrayOf(name,height,weight,age,sex.toString())
                DB.updateRecord(DBContract.UserInfo.TABLE_NAME,str,str2,"${DBContract.UserInfo.NAME} = ?",arrayOf(userInfo?.get(1).toString()))

                //fragmentの張り替え
                val fragmentManager1 = fragmentManager
                val transaction1 = fragmentManager1!!.beginTransaction()
                transaction1.replace(R.id.frame_contents,FinishEditInfFragment())
                transaction1.commit()
            }

        }
        //キャンセルが押されたときの処理
        btn2.setOnClickListener{
            var isValid2=true

            if(isValid2){
                val fragmentManager2 = fragmentManager
                val transaction2 = fragmentManager2!!.beginTransaction()

                transaction2.replace(R.id.frame_contents,RegistrationInfEditFragment())
                transaction2.commit()
            }
        }






        return v
    }







}
package com.example.wse2019



import android.support.v4.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.example.sample.SampleDBOpenHelper
import com.example.sample.DBContract
import android.support.v7.app.AlertDialog
import android.content.DialogInterface
import android.widget.*
import com.example.sample.Table
import android.view.WindowManager


private const val ADD="+"


class MyCondateFragment(): Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_my_condate, container,false)


        val DB = SampleDBOpenHelper(requireContext())
        //リストビューを探す
        val myListView=v.findViewById<ListView>(R.id.listView1)

        //リストビューで表示

        var items=loadList(DB)

        val adapter =ArrayAdapter(context,android.R.layout.simple_list_item_1,items)
        myListView.adapter=adapter







        // 項目をタップしたときの処理
        myListView.setOnItemClickListener {parent,view, position, id ->

            var isValid=true

            val condateName=adapter.getItem(position)

            //画面遷移処理
            if(isValid){
                if(condateName==ADD){
                    //項目追加
                    val dialog=inflater.inflate(R.layout.dialog_create_my_condate,null,false)
                    val dialogEditText : EditText = dialog.findViewById(R.id.dialogEditText)

                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("追加する献立名を入力してください。")
                        setView(dialog)
                        setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                            // OKボタンを押したときの処理

                            val newCondateName=dialogEditText.text.toString()

                            val tf=DB.insertRecord(
                                Table.MyCondate(newCondateName)
                            )
                            if(tf!=true){
                                TODO()
                            }

                            adapter.clear()
                            items=loadList(DB)
                            adapter.addAll(items)

                            Toast.makeText(context, "${newCondateName}を登録しました。", Toast.LENGTH_LONG).show()
                        })
                        setNegativeButton("Cancel", null)
                        show()
                    }
                }else{
                    //テキスト名をfragmentにセット
                    val bundle= Bundle()

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

            adapter.notifyDataSetChanged()

        }

        // 項目を長押ししたときの処理
        myListView.setOnItemLongClickListener { parent, view, position, id ->

            val condateName2=adapter.getItem(position)

            if(condateName2==ADD){
                return@setOnItemLongClickListener true
            }

            AlertDialog.Builder(requireContext()).apply {
                setTitle("${condateName2}を削除しますか?")
                setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    // OKをタップしたときの処理

                    //長押しされたとき、項目を削除する

                    DB.deleteRecord(DBContract.MyCondate.TABLE_NAME,"${DBContract.MyCondate.NAME} = ?",arrayOf(condateName2))

                    adapter.clear()
                    items=loadList(DB)
                    adapter.addAll(items)

                    Toast.makeText(context, "${condateName2}を削除しました。", Toast.LENGTH_LONG).show()
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
    private fun loadList(DB:SampleDBOpenHelper):ArrayList<String>{
        //献立を配列に入れる


        val columns = arrayOf(DBContract.MyCondate.NAME)
        val result = DB.searchRecord(DBContract.MyCondate.TABLE_NAME, columns)?: TODO()
        val size=result.size
        val items=ArrayList<String>()



        var i=0
        while(i<size){
            items.add(result[i])
            i=i+1
        }
        //最後の項目に+を表示
        items.add(ADD)

        return items


    }

}
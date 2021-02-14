package com.example.wse2019

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import java.util.*
import kotlin.NullPointerException

class CondateEditFragment(): Fragment() {

    // フラグ用
    val ON  = 1
    val OFF = 0

    val OK = 1
    val NG = 0

    // 使用する画像ID
    private val favoriteButton = listOf(
        android.R.drawable.btn_star_big_off,
        android.R.drawable.btn_star_big_on
    )

    // アダプター
    private lateinit var condateEditAdapter : CondateEditAdapter
    private lateinit var categoryAdapter    : ArrayAdapter<String>
    private lateinit var searchAdapter      : FoodSearchResultAdapter

    private var condateName : String

    init {
        condateName = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if(bundle!=null){
            condateName = bundle.getString("MYCONDATE_NAME")
        }

        categoryAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1).apply {

            add("カテゴリ")
            addAll(CategoryManager().getCategories(requireContext()))
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        searchAdapter = FoodSearchResultAdapter(requireContext()).apply {
            updateResult()
        }
        condateEditAdapter = CondateEditAdapter(requireContext())
    }



    // ------------------------------------------------------------
    //  描画
    // ------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_condate_edit, container, false)

        val fm = FoodManager()




        // ------------------------------------------------------------
        //  検索フィールド
        // ------------------------------------------------------------
        val searchEditText: EditText = v.findViewById(R.id.searchEditText)
        searchEditText.apply {
            hint = "品目名"
            allowEnterTransitionOverlap = false
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    searchAdapter.updateCondition(name = text.toString())
                    searchAdapter.updateResult()
                }

                override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) { /* なにもしない */ }
                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) { /* なにもしない */ }
            })
        }

        // ------------------------------------------------------------
        //  カテゴリ選択
        // ------------------------------------------------------------
        val category: Spinner   = v.findViewById(R.id.categorySpinner)
        category.apply {
            adapter = categoryAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    searchAdapter.updateCondition(category = position)
                    searchAdapter.updateResult()
                }
                override fun onNothingSelected(pstrmy: AdapterView<*>?) {
                    // なにもしない
                }
            }
        }


        // ------------------------------------------------------------
        //  お気に入り絞り込みボタン
        // ------------------------------------------------------------
        val favorite: ImageButton = v.findViewById(R.id.favoriteFoodImageButton)
        favorite.apply {
            setOnClickListener {
                when (searchAdapter.condition.favorite) {
                    ON  -> searchAdapter.updateCondition(favorite = OFF)
                    OFF -> searchAdapter.updateCondition(favorite = ON)
                }
                favorite.setImageResource(favoriteButton[searchAdapter.condition.favorite])
                searchAdapter.updateResult()
            }
        }

        // ------------------------------------------------------------
        //  検索結果
        // ------------------------------------------------------------
        val result: ListView    = v.findViewById(R.id.foodSearchResultListView)
        result.apply {
            adapter = searchAdapter
            setOnItemClickListener { parent, view, position, id ->
                val food: FoodSearchResultAdapter.Food = searchAdapter.getItem(position)
                when (id.toInt()) {
                    R.id.favoriteButton -> {
                        searchAdapter.switchFavorite(position)
                        searchAdapter.updateResult()
                    }
                    R.id.addButton -> {
                        showNumberPickerDialog(food)
                    }
                    else -> {
                        AlertDialog.Builder(context).apply {
                            setView(fm.getFoodInformationView(food.id, context, parent))
                            show()
                        }

                    }
                }
            }
        }



        // ------------------------------------------------------------
        //  登録状況
        // ------------------------------------------------------------
        val stateListView: ListView = v.findViewById(R.id.registrationStateListView)
        stateListView.apply {
            adapter = condateEditAdapter
            setOnItemClickListener { parent, view, position, id ->
                val food: CondateEditAdapter.Food = condateEditAdapter.getItem(position)

                when (id.toInt()) {

                    // ×ボタンがタップされた場合
                    /*
                    確認のダイアログを表示して、OKされれば削除する
                     */
                    R.id.removeImageButton -> {
                        AlertDialog.Builder(context).apply {
                            setMessage("${food.name}[${food.number}人前]を仮登録状況から削除します。よろしいですか？")
                            setPositiveButton("OK") { _, _ ->
                                condateEditAdapter.remove(position)
                                condateEditAdapter.notifyDataSetChanged()
                            }
                            setNegativeButton("キャンセル", null)
                            show()
                        }
                    }
                }

            }
        }

        // ------------------------------------------------------------
        //  「食べたい品目が見つかりませんか？」ボタン
        // ------------------------------------------------------------
        val registerNewFoodButton: Button = v.findViewById(R.id.registerNewFoodButton)
        registerNewFoodButton.apply {
            setOnClickListener {
                val foodRegistrationFragment = FoodRegistrationFragment()
                val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
                ft.addToBackStack(null)
                ft.replace(R.id.frame_contents, foodRegistrationFragment)
                ft.commit()
            }
        }

        // ------------------------------------------------------------
        //  登録ボタン
        // ------------------------------------------------------------
        val register: Button = v.findViewById(R.id.registerButton)
        register.apply {
            setOnClickListener {
                val foods: MutableList<Pair<Int, Float>> = mutableListOf()
                for (i in 0 until condateEditAdapter.count) {
                    val food = condateEditAdapter.getItem(i)
                    foods.add(Pair(food.id, food.number))
                }

                when (registrationMyCondate(condateName,foods)) {
                    OK -> {
                        fragmentManager?.popBackStack() ?: throw NullPointerException()
                        Toast.makeText(context, "My献立に登録しました", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }



    // ------------------------------------------------------------
    //  showNumberPickerDialog
    //  - 選択した品目を何人前登録するかの選択ダイアログを表示
    // ------------------------------------------------------------
    private fun showNumberPickerDialog(food: FoodSearchResultAdapter.Food) {

        class NumberPickerDialog : DialogFragment() {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val inflater: LayoutInflater = activity?.layoutInflater ?: throw NullPointerException()
                val v: View = inflater.inflate(R.layout.number_picker_dialog, null, false)

                val intNPicker: NumberPicker = v.findViewById(R.id.intNumberPicker)
                val decNPicker: NumberPicker = v.findViewById(R.id.decNumberPicker)
                intNPicker.maxValue = 9
                decNPicker.maxValue = 9
                intNPicker.value = 1

                val builder = AlertDialog.Builder(activity)
                builder
                    .setTitle("何人前か選択してください")
                    .setPositiveButton("OK") { _, _ ->
                        when (val number: Float = formatNumber(intNPicker.value, decNPicker.value)) {
                            0f      -> Toast.makeText(context, "0より大きい値を入力してください", Toast.LENGTH_SHORT).show()
                            else    -> {
                                condateEditAdapter.add(CondateEditAdapter.Food(food.id, food.name, number))
                                condateEditAdapter.notifyDataSetChanged()
                            }
                        }}
                    .setNegativeButton("キャンセル", null)
                    .setView(v)

                return builder.create()
            }

            private fun formatNumber(int: Int, dec: Int) : Float {
                return when (dec) {
                    0       -> int.toFloat()
                    else    -> "${int}.${dec}".toFloat()
                }
            }
        }
        val dialog = NumberPickerDialog()
        dialog.show(fragmentManager, "setting_dialog")
    }
    // ------------------------------------------------------------





    // --------------------------------------------------
    //  registerMyCondate
    //  - 品目をMy献立に追加する
    // --------------------------------------------------
    /*
     * @params
     *      name    : String(My献立名)
     *      foods   : List<Pair<Int(品目ID), Float(何人前)>>
     */
    private fun registrationMyCondate(name: String, foods: List<Pair<Int, Float>>): Int {

        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }
        val myCondateT = DBContract.MyCondate

        // 追加したMy献立のIDを取得
        val myCondateId = db.searchRecord(
            tableName = myCondateT.TABLE_NAME,
            column = arrayOf(myCondateT.ID),
            condition = "${myCondateT.NAME}= ?",
            selectionArgs = arrayOf(name)

        )?.last()?.toInt() ?: throw NullPointerException()

        // 各品目を、追加したMy献立に登録
        for (i in 0 until foods.size) {
            val (foodId, number) = foods[i]
            db.insertRecord(Table.MyCondate_Food(
                myCondateId, foodId, number
            ))
        }

        return OK
    }
    // --------------------------------------------------



}
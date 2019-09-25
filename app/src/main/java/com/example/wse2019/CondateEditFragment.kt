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

    // 検索条件を定義
    data class Condition(
        var name    : String    = "",
        var favorite: Int       = 0,    // OFF
        var category: Int       = 0     // unselected
    )

    // 使用する画像ID

    private val favoriteButton = listOf(
        android.R.drawable.btn_star_big_off,
        android.R.drawable.btn_star_big_on
    )

    lateinit private var search         : Search        // 検索管理用クラス
    lateinit private var state          : State         // 仮登録状況管理用クラス

    private var condateName=""





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ----- 初期化 -----
        search.init()
        state.init()

        val bundle=arguments
        if(bundle!=null){
            condateName=bundle.getString("MYCONDATE_NAME")
        }
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
                    search.updateCondition(name = text.toString())
                    search.updateResult()
                }

                override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) { /* なにもしない */ }
                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) { /* なにもしない */ }
            })
        }

        // ------------------------------------------------------------
        //  カテゴリ選択
        // ------------------------------------------------------------
        val category: Spinner   = v.findViewById(R.id.categorySpinner)
        val categoryAdapter     = when (context) {
            null -> throw NullPointerException()
            else -> ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1)}
        val cm = CategoryManager()
        categoryAdapter.apply {
            add("カテゴリ") // ヒントであると同時に "未選択 (全カテゴリ)" を意味
            addAll(cm.getCategories(context))
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        category.apply {
            adapter = categoryAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    search.updateCondition(category = position)
                    search.updateResult()
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
                when (search.condition.favorite) {
                    ON  -> search.updateCondition(favorite = OFF)
                    OFF -> search.updateCondition(favorite = ON)
                }
                favorite.setImageResource(favoriteButton[search.condition.favorite])
                search.updateResult()
            }
        }

        // ------------------------------------------------------------
        //  検索結果
        // ------------------------------------------------------------
        val result: ListView    = v.findViewById(R.id.foodSearchResultListView)
        result.apply {
            adapter = search.getAdapter()
            setOnItemClickListener { parent, view, position, id ->
                val food: FoodSearchResultAdapter.Food = search.getResultItem(position)
                when (id.toInt()) {
                    R.id.favoriteButton -> {
                        search.switchFavorite(position)
                        search.updateResult()
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
        //  仮登録状況
        // ------------------------------------------------------------
        val stateListView: ListView = v.findViewById(R.id.registrationStateListView)
        stateListView.apply {
            adapter = state.getAdapter()
            setOnItemClickListener { parent, view, position, id ->
                val food: TempRegistrationStateAdapter.Food = state.getItem(position)

                when (id.toInt()) {

                    // ×ボタンがタップされた場合
                    /*
                    確認のダイアログを表示して、OKされれば削除する
                     */
                    R.id.removeImageButton -> {
                        AlertDialog.Builder(context).apply {
                            setMessage("${food.name}[${food.number}人前]を仮登録状況から削除します。よろしいですか？")
                            setPositiveButton("OK") { _, _ ->
                                state.remove(position)
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
                for (i in 0 until state.count) {
                    val food = state.getItem(i)
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

        search  = Search()
        state   = State()
    }
    override fun onDetach() {
        super.onDetach()
    }




    // ==================================================
    // ==================================================
    //
    //  Search
    //  - 検索を管理するクラス
    //
    // ==================================================
    // ==================================================
    private inner class Search {
        private val adapter: FoodSearchResultAdapter = when (context) {
            null -> throw NullPointerException()
            else -> FoodSearchResultAdapter(context!!)
        }
        val condition = Condition()
        val count: Int
            get() = adapter.count

        fun getAdapter() : FoodSearchResultAdapter { return adapter }
        fun getResultItem(position: Int) : FoodSearchResultAdapter.Food { return adapter.getItem(position) }

        fun init() {

            updateResult()

        }

        fun updateResult() {

            adapter.clear()
            adapter.searchFoods(
                condition.name,
                condition.favorite,
                condition.category
            )
            adapter.notifyDataSetChanged()

        }

        fun updateCondition(
            name    : String? = null,
            favorite: Int?    = null,
            category: Int?    = null
        ) {
            if (name        != null) { condition.name      = name }
            if (favorite    != null) { condition.favorite  = favorite}
            if (category    != null) { condition.category  = category}
        }

        fun switchFavorite(position: Int) {
            adapter.switchFavorite(position)
        }

    }



    // ==================================================
    // ==================================================
    //
    //  State
    //  - 仮登録状況を管理するクラス
    //
    // ==================================================
    // ==================================================
    private inner class State {
        private val adapter: TempRegistrationStateAdapter = when (context) {
            null -> throw NullPointerException()
            else -> TempRegistrationStateAdapter(context!!)
        }
        val count: Int
            get() = adapter.count
        val isEmpty: Boolean
            get() = adapter.count == 0

        fun getAdapter() : TempRegistrationStateAdapter { return adapter }
        fun getItem(position: Int) : TempRegistrationStateAdapter.Food { return adapter.getItem(position) }

        fun init() {

        }


        fun add(food: TempRegistrationStateAdapter.Food) {
            adapter.add(food)
            adapter.notifyDataSetChanged()
        }

        fun addAll(foods: List<TempRegistrationStateAdapter.Food>) {

            foods.forEach {
                adapter.add(it)
            }
            adapter.notifyDataSetChanged()

        }

        fun remove(position: Int) {
            adapter.remove(position)
            adapter.notifyDataSetChanged()
        }

        fun clear() {
            adapter.clear()
        }

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
                                state.add(TempRegistrationStateAdapter.Food(food.id, food.name, number))
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

        )?.max()?.toInt() ?: throw NullPointerException()

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
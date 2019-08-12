package com.example.wse2019

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import kotlinx.android.synthetic.main.fragment_top.*
import java.util.*
import kotlin.NullPointerException

class CondateRegistrationFragment(): Fragment() {

    // フラグ用
    val ON  = 1
    val OFF = 0

    // 時間帯を定義
    enum class Day(val str: String) {
        YEAR    ("year"),
        MONTH   ("month"),
        DATE    ("date"),
        TIME    ("time")
    }

    // 検索条件を定義
    data class Condition(
        var name    : String    = "",
        var favorite: Int       = 0,    // OFF
        var category: Int       = 0     // unselected
    )
    // 品目登録状況を管理
    data class State(
        val food: FoodSearchResultAdapter.Food,
        val number: Float
    )

    val timeRadioButtonID: List<Int> = listOf<Int>(
        R.id.morningRadioButton,
        R.id.noonRadioButton,
        R.id.eveningRadioButton,
        R.id.snackRadioButton)
    val favoriteButton = listOf<Int>(

    )

    lateinit private var fsrAdapter : FoodSearchResultAdapter   // 検索結果の操作用アダプター
    lateinit private var stAdapter  : ArrayAdapter<String>      // 品目登録状況の操作用アダプター

    private var day: MutableMap<Day, Int>   = mutableMapOf()  // 日時
    private val condition                   = Condition()     // 検索条件

    // ------------------------------------------------------------
    //  値の取得
    // ------------------------------------------------------------
    companion object {
        fun newInstance(
            year    : Int,
            month   : Int,
            date    : Int,
            time    : Int) : CondateRegistrationFragment
        {
            val f = CondateRegistrationFragment()
            val args = Bundle()
            args.putInt(Day.YEAR.str,   year)
            args.putInt(Day.MONTH.str,  month)
            args.putInt(Day.DATE.str,   date)
            args.putInt(Day.TIME.str,   time)
            f.arguments = args
            return f
        }
    }

    // ------------------------------------------------------------
    //  初期化
    // ------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ----- 引数を配列に格納 -----
        val args: Bundle? = arguments
        when {
            args != null -> {
                Day.values().forEach { day ->
                    this.day.put(day, args.getInt(day.str))
                }
            }
        }
        // ----- 初期化 -----
        searchFoods()
        updateState(this.day)
    }

    // ------------------------------------------------------------
    //  描画
    // ------------------------------------------------------------
    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_condate_registration, container, false)

        // ------------------------------------------------------------
        //  日付
        // ------------------------------------------------------------
        val day: TextView = v.findViewById(R.id.dayTextView)
        // ----- 初期値 -----
        day.text = when (this.day.isEmpty()) {
            true    -> "選択してください"
            false   -> "${this.day[Day.YEAR]}/${this.day[Day.MONTH]}/${this.day[Day.DATE]}"
        }
        day.setOnClickListener {
            // カレンダーダイアログで日付を選択
            val date: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context ?: throw NullPointerException(),
                DatePickerDialog.OnDateSetListener { _, year, month, date ->

                    this.day[Day.YEAR]  = year
                    this.day[Day.MONTH] = month.inc()
                    this.day[Day.DATE]  = date
                    day.text = "%d/%d/%d".format(
                        this.day[Day.YEAR],
                        this.day[Day.MONTH],
                        this.day[Day.DATE]
                    )
                    updateState(day = this.day)

                },
                this.day[Day.YEAR] ?: date.get(Calendar.YEAR),
                this.day[Day.MONTH]?.dec() ?: date.get(Calendar.MONTH),
                this.day[Day.DATE] ?: date.get(Calendar.DATE)
            )
            datePickerDialog.show()
        }

        // ------------------------------------------------------------
        //  時間帯
        // ------------------------------------------------------------
        val time: RadioGroup = v.findViewById(R.id.timeRadioGroup)
        time.check(when (this.day.isEmpty()) {
            true    -> -1
            false   -> this.timeRadioButtonID[this.day[Day.TIME]!!]
        })
        time.setOnCheckedChangeListener { _, checkedId ->
            this.day[Day.TIME] = this.timeRadioButtonID.indexOf(checkedId)
            updateState(day = this.day)
        }

        // ------------------------------------------------------------
        //  検索フィールド
        // ------------------------------------------------------------
        val foodSearchView: SearchView = v.findViewById(R.id.foodSearchView)
        foodSearchView.setIconifiedByDefault(false)
        foodSearchView.queryHint = "品目名"
        foodSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(query: String?): Boolean {
                updateCondition(name = query)
                searchFoods()
                return false
            }
        })

        // ------------------------------------------------------------
        //  カテゴリ選択
        // ------------------------------------------------------------
        val category: Spinner   = v.findViewById(R.id.categorySpinner)
        val adapter             = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        category.adapter = adapter
        adapter.add("カテゴリ") // ヒントであると同時に "未選択 (全カテゴリ)" を意味
        adapter.addAll(getCategories())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                updateCondition(category = position)
                searchFoods()
            }
            override fun onNothingSelected(pstrmy: AdapterView<*>?) {
                // なにもしない
            }
        }

        // ------------------------------------------------------------
        //  お気に入り絞り込みボタン
        // ------------------------------------------------------------
        val favorite: ImageButton = v.findViewById(R.id.favoriteFoodImageButton)
        favorite.setOnClickListener { when (this.condition.favorite) {
            ON -> {
                Toast.makeText(context, "お気に入りにによる絞り込みを OFF にします", Toast.LENGTH_SHORT).show()
                updateCondition(favorite = OFF)
                favorite.setImageResource(android.R.drawable.btn_star_big_off)
                searchFoods()
            }
            OFF -> {
                Toast.makeText(context, "お気に入りにによる絞り込みを ON にします", Toast.LENGTH_SHORT).show()
                updateCondition(favorite = ON)
                favorite.setImageResource(android.R.drawable.btn_star_big_on)
                searchFoods()
            }
        }}

        // ------------------------------------------------------------
        //  検索結果
        // ------------------------------------------------------------
        val result: ListView    = v.findViewById(R.id.foodSearchResultListView)
        result.adapter = this.fsrAdapter
        result.setOnItemClickListener { parent, view, position, id ->
            val food: FoodSearchResultAdapter.Food = this.fsrAdapter.getItem(position)
            when (id.toInt()) {
                R.id.favoriteButton -> {
                    this.fsrAdapter.switchFavorite(position)
                    searchFoods()
                }
                R.id.addButton -> {
                    showNumberPickerDialog(food)
                }
            }
        }

        // ------------------------------------------------------------
        //  My献立ボタン
        // ------------------------------------------------------------
        val myCondate: Button = v.findViewById(R.id.myCondateButton)
        myCondate.setOnClickListener {
            Toast.makeText(context, "My献立ボタンをおしました", Toast.LENGTH_SHORT).show()

            /***** ここに記述 *****/

        }

        // ------------------------------------------------------------
        //  登録状況
        // ------------------------------------------------------------
        val state: ListView = v.findViewById(R.id.registrationStateListView)
        state.adapter = stAdapter

        // ------------------------------------------------------------
        //  登録ボタン
        // ------------------------------------------------------------
        val register: Button = v.findViewById(R.id.registerButton)
        register.setOnClickListener {
            Toast.makeText(context, "登録ボタンをおしました", Toast.LENGTH_SHORT).show(
            )

            /***** ここに記述 *****/

        }

        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        // ----- アダプターの初期化 ------
        this.fsrAdapter = when (context) {
            null -> throw NullPointerException()
            else -> FoodSearchResultAdapter(context)
        }
        this.stAdapter  = when (context) {
            null -> throw NullPointerException()
            else -> ArrayAdapter(context, android.R.layout.simple_list_item_1)
        }

    }
    override fun onDetach() {
        super.onDetach()
    }

    private fun getCategories() : List<String> {
        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }
        val categoryTable = DBContract.Category

        return db.searchRecord(
            tableName   = categoryTable.TABLE_NAME,
            column      = arrayOf(
                categoryTable.NAME
            )
        ) ?: throw NullPointerException("searchRecord was failed")
    }

    private fun updateCondition(
        name    : String? = null,
        favorite: Int?    = null,
        category: Int?    = null
    ) {
        if (name        != null) { this.condition.name      = name }
        if (favorite    != null) { this.condition.favorite  = favorite}
        if (category    != null) { this.condition.category  = category}
    }

    private fun searchFoods() {
        this.fsrAdapter.searchFoods(
            this.condition.name,
            this.condition.favorite,
            this.condition.category
        )
    }

    // --------------------------------------------------
    //  登録状況の更新
    // --------------------------------------------------
    private fun updateState(
        day: MutableMap<Day, Int>?   = null,
        state: State?                = null
    ) {
        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }
        var result = mutableListOf<String>()

        //  日付が変更された場合
        // --------------------------------------------------
        if (day != null) {
            val recordTable = DBContract.Record
            val foodTable = DBContract.Food

            // 検索
            val result: List<String> = db.searchRecord(
                tableName   = foodTable.TABLE_NAME,
                column      = arrayOf(
                    foodTable.NAME,
                    recordTable.NUMBER
                ),
                condition   =       "${recordTable.TABLE_NAME}.${recordTable.YEAR}  = ${day[Day.YEAR]}" +
                        " and " +   "${recordTable.TABLE_NAME}.${recordTable.MONTH} = ${day[Day.MONTH]}" +
                        " and " +   "${recordTable.TABLE_NAME}.${recordTable.DATE}  = ${day[Day.DATE]}" +
                        " and " +   "${recordTable.TABLE_NAME}.${recordTable.TIME}  = ${day[Day.TIME]}",
                innerJoin   = Join(
                    tablename   = recordTable.TABLE_NAME,
                    column1     = foodTable.ID,
                    column2     = recordTable.FOOD_ID
                )) ?: throw NullPointerException("searchRecord was failed.")

            // 格納
            var i = 0
            this.stAdapter.clear()
            while (i < result.size) {
                this.stAdapter.add("${result[i++]}[${result[i++]}人前]")
            }
        }
        // --------------------------------------------------

        //  品目が追加された場合
        // --------------------------------------------------
        if (state != null) {
            this.stAdapter.add("${state.food.name}[${state.number}人前]")
        }
        // --------------------------------------------------

        this.stAdapter.notifyDataSetChanged()
    }


    // --------------------------------------------------
    //  何人前か表示するダイアログ
    // --------------------------------------------------
    private fun showNumberPickerDialog(food: FoodSearchResultAdapter.Food) {

        class NumberPickerDialog : DialogFragment() {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val inflater: LayoutInflater = activity?.layoutInflater ?: throw NullPointerException()
                val v: View = inflater.inflate(R.layout.number_picker_dialog, null, false)

                val intNPicker: NumberPicker = v.findViewById(R.id.intNumberPicker)
                val decNPicker: NumberPicker = v.findViewById(R.id.decNumberPicker)
                intNPicker.maxValue = 9
                decNPicker.maxValue = 9

                val builder = AlertDialog.Builder(activity)
                builder
                    .setTitle("何人前か選択してください")
                    .setPositiveButton("OK") { _, _ ->
                        when (val number: Float = formatNumber(intNPicker.value, decNPicker.value)) {
                            0f      -> Toast.makeText(context, "0より大きい値を入力してください", Toast.LENGTH_SHORT).show()
                            else    -> updateState(state = State(food, number))
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

}
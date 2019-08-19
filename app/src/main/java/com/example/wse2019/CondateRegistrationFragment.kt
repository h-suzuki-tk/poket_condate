package com.example.wse2019

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import kotlinx.android.synthetic.main.fragment_top.*
import java.util.*
import kotlin.NullPointerException
import kotlin.collections.ArrayList

class CondateRegistrationFragment(): Fragment() {

    // フラグ用
    val ON  = 1
    val OFF = 0

    val OK = 1
    val NG = 0

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

    val timeRadioButtonID: List<Int> = listOf<Int>(
        R.id.morningRadioButton,
        R.id.noonRadioButton,
        R.id.eveningRadioButton,
        R.id.snackRadioButton)
    val favoriteButton = listOf<Int>(

    )


    private var selectedDay: MutableMap<Day, Int>   = mutableMapOf()  // 日時

    lateinit private var search         : Search        // 検索管理用クラス
    lateinit private var state          : State         // 仮登録状況管理用クラス



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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ----- 引数を配列に格納 -----
        val args: Bundle? = arguments
        when {
            args != null -> {
                Day.values().forEach { day ->
                    selectedDay.put(day, args.getInt(day.str))
                }
            }
        }
        // ----- 初期化 -----
        search.init()
        state.init()
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
        day.text = when (selectedDay.isEmpty()) {
            true    -> "選択してください"
            false   -> "${selectedDay[Day.YEAR]}/${selectedDay[Day.MONTH]}/${selectedDay[Day.DATE]}"
        }
        day.setOnClickListener {
            // カレンダーダイアログで日付を選択
            val date: Calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                context ?: throw NullPointerException(),
                DatePickerDialog.OnDateSetListener { _, year, month, date ->

                    selectedDay[Day.YEAR]  = year
                    selectedDay[Day.MONTH] = month.inc()
                    selectedDay[Day.DATE]  = date
                    day.text = "%d/%d/%d".format(
                        selectedDay[Day.YEAR],
                        selectedDay[Day.MONTH],
                        selectedDay[Day.DATE]
                    )

                    state.addAll(state.searchFoods(selectedDay))
                    state.notifyDataSetChanged()
                },
                selectedDay[Day.YEAR] ?: date.get(Calendar.YEAR),
                selectedDay[Day.MONTH]?.dec() ?: date.get(Calendar.MONTH),
                selectedDay[Day.DATE] ?: date.get(Calendar.DATE)
            )
            datePickerDialog.show()
        }

        // ------------------------------------------------------------
        //  時間帯
        // ------------------------------------------------------------
        val time: RadioGroup = v.findViewById(R.id.timeRadioGroup)
        time.check(when (selectedDay.isEmpty()) {
            true    -> -1
            false   -> timeRadioButtonID[selectedDay[Day.TIME]!!]
        })
        time.setOnCheckedChangeListener { _, checkedId ->
            selectedDay[Day.TIME] = timeRadioButtonID.indexOf(checkedId)
            state.addAll(state.searchFoods(selectedDay))
            state.notifyDataSetChanged()
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
                search.updateCondition(name = query)
                search.updateResult()
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
                search.updateCondition(category = position)
                search.updateResult()
            }
            override fun onNothingSelected(pstrmy: AdapterView<*>?) {
                // なにもしない
            }
        }

        // ------------------------------------------------------------
        //  お気に入り絞り込みボタン
        // ------------------------------------------------------------
        val favorite: ImageButton = v.findViewById(R.id.favoriteFoodImageButton)
        favorite.setOnClickListener { when (search.condition.favorite) {
            ON -> {
                search.updateCondition(favorite = OFF)
                favorite.setImageResource(android.R.drawable.btn_star_big_off)
                search.updateResult()
            }
            OFF -> {
                search.updateCondition(favorite = ON)
                favorite.setImageResource(android.R.drawable.btn_star_big_on)
                search.updateResult()
            }
        }}

        // ------------------------------------------------------------
        //  検索結果
        // ------------------------------------------------------------
        val result: ListView    = v.findViewById(R.id.foodSearchResultListView)
        result.adapter = search.getAdapter()
        result.setOnItemClickListener { parent, view, position, id ->
            val food: FoodSearchResultAdapter.Food = search.getResultItem(position)
            when (id.toInt()) {
                R.id.favoriteButton -> {
                    search.switchFavorite(position)
                    search.updateResult()
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
            showMyCondateDialog(v)
        }

        // ------------------------------------------------------------
        //  仮登録状況
        // ------------------------------------------------------------
        val stateListView: ListView = v.findViewById(R.id.registrationStateListView)
        stateListView.adapter = state.getAdapter()
        stateListView.setOnItemClickListener { parent, view, position, id ->
            val food: TempRegistrationStateAdapter.Food = state.getItem(position)

            when (id.toInt()) {

                // ×ボタンがタップされた場合
                /*
                確認のダイアログを表示して、OKされれば削除する
                 */
                R.id.removeImageButton -> {
                    AlertDialog.Builder(context)
                        .setMessage("${food.name}[${food.number}人前]を仮登録状況から削除します。よろしいですか？")
                        .setPositiveButton("OK") { _, _ ->
                            state.remove(position)
                            state.notifyDataSetChanged()
                        }
                        .setNegativeButton("キャンセル", null)
                        .show()
                }
            }

        }

        // ------------------------------------------------------------
        //  登録ボタン
        // ------------------------------------------------------------
        val register: Button = v.findViewById(R.id.registerButton)
        register.setOnClickListener {
            when (registerCondate()) {
                OK      -> {
                    fragmentManager?.popBackStack() ?: throw NullPointerException()
                    Toast.makeText(context, "登録しました", Toast.LENGTH_SHORT).show()
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
            Log.d("condition", condition.toString())
            if (name        != null) { condition.name      = name }
            if (favorite    != null) { condition.favorite  = favorite}
            if (category    != null) { condition.category  = category}
            Log.d("condition", condition.toString())
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

        fun getAdapter() : TempRegistrationStateAdapter { return adapter }
        fun getItem(position: Int) : TempRegistrationStateAdapter.Food { return adapter.getItem(position) }

        fun init() {

            if (selectedDay.isNotEmpty()) {
                val registeredFoods: List<TempRegistrationStateAdapter.Food> = searchFoods(selectedDay)
                if (registeredFoods.isNotEmpty()) {
                    addAll(registeredFoods)
                }
            }

        }

        fun searchFoods(day: MutableMap<Day, Int>) : List<TempRegistrationStateAdapter.Food> {

            // データベース検索のための宣言・初期化
            val db = SampleDBOpenHelper(context)
            val recordT = DBContract.Record // Record table
            val foodT = DBContract.Food // Food table

            // 検索
            val result: List<String> = db.searchRecord(
                tableName = foodT.TABLE_NAME,
                column = arrayOf(
                    "${foodT.TABLE_NAME}.${foodT.ID}",
                    foodT.NAME,
                    recordT.NUMBER
                ),
                condition = "${recordT.TABLE_NAME}.${recordT.YEAR}  = ${day[Day.YEAR]}" +
                        " and " + "${recordT.TABLE_NAME}.${recordT.MONTH} = ${day[Day.MONTH]}" +
                        " and " + "${recordT.TABLE_NAME}.${recordT.DATE}  = ${day[Day.DATE]}" +
                        " and " + "${recordT.TABLE_NAME}.${recordT.TIME}  = ${day[Day.TIME]}",
                innerJoin = Join(
                    tablename = recordT.TABLE_NAME,
                    column1 = foodT.ID,
                    column2 = recordT.FOOD_ID
                )
            ) ?: throw NullPointerException("searchRecord was failed.")

            // 検索結果を格納
            val foods: MutableList<TempRegistrationStateAdapter.Food> = mutableListOf()
            var i = 0
            while (i < result.size) {
                foods.add(
                    TempRegistrationStateAdapter.Food(
                        id = result[i++].toInt(),
                        name = result[i++],
                        number = result[i++].toFloat()
                    )
                )
            }

            return foods
        }

        fun add(food: TempRegistrationStateAdapter.Food) {
            adapter.add(food)
        }

        fun addAll(foods: List<TempRegistrationStateAdapter.Food>) {

            foods.forEach {
                adapter.add(it)
            }

        }

        fun notifyDataSetChanged() {
            adapter.notifyDataSetChanged()
        }

        fun remove(position: Int) {
            adapter.remove(position)
        }

        fun clear() {
            adapter.clear()
        }

    }



    // ==================================================
    // ==================================================
    //
    //  showNumberPickerDialog
    //  - 何人前を登録するか指定するダイアログを表示する
    //
    // ==================================================
    // ==================================================
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
                            else    -> {
                                state.add(TempRegistrationStateAdapter.Food(food.id, food.name, number))
                                state.notifyDataSetChanged()
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


    // ==================================================
    // ==================================================
    //
    //  registerCondate
    //  - 献立を登録する
    //
    // ==================================================
    // ==================================================
    private fun registerCondate(): Int {

        // ----- 登録内容の確認 -----
        if (listOf(
                selectedDay[Day.YEAR],
                selectedDay[Day.MONTH],
                selectedDay[Day.DATE],
                selectedDay[Day.TIME]).contains(null)) {
            Toast.makeText(context, "日時を指定してください", Toast.LENGTH_SHORT).show()
            return NG
        }
        if (state.count == 0) {
            Toast.makeText(context, "１つ以上の品目を登録してください", Toast.LENGTH_SHORT).show()
            return NG
        }

        // ----- データベースに追加 -----
        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }

        for (i in 0..state.count.minus(1)) {
            val food = state.getItem(i)
            db.insertRecord(
                Table.Record(
                    food_id = food.id,
                    year    = selectedDay[Day.YEAR]!!,
                    month   = selectedDay[Day.MONTH]!!,
                    date    = selectedDay[Day.DATE]!!,
                    time    = selectedDay[Day.TIME]!!,
                    number  = food.number
                )
            )
        }

        return OK

    }


    // ==================================================
    // ==================================================
    //
    //  showMyCondateDialog
    //  - マイ献立を選択するダイアログを表示する
    //
    // ==================================================
    // ==================================================
    private fun showMyCondateDialog(view: View) {

        // My献立情報を管理
        data class MyCondate(
            val id  : Int,
            val name: String
        )
        // 各My献立の行で表示する情報
        data class ViewHolderItem(
            var name    : TextView,
            val contents: TextView
        )

        // --------------------------------------------------
        //  ダイアログの設定
        // --------------------------------------------------
        class MyCondateAdapter(context: Context) : BaseAdapter() {

            private val myCondate: MutableList<MyCondate> = mutableListOf()

            override fun getCount(): Int {
                return myCondate.size
            }

            override fun getItem(position: Int): Any {
                return myCondate[position]
            }

            override fun getItemId(position: Int): Long {
                return myCondate[position].id.toLong()
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                val (viewHolder, view) = when (convertView) {

                    null -> {
                        val view = LayoutInflater.from(context).inflate(R.layout.my_condate_row, parent, false)
                        val viewHolder = ViewHolderItem(
                            name        = view.findViewById(R.id.nameTextView),
                            contents    = view.findViewById(R.id.contentsTextView)
                        )
                        view.tag = viewHolder
                        viewHolder as ViewHolderItem to view
                    }
                    else -> convertView.tag as ViewHolderItem to convertView

                }

                viewHolder.name.text = myCondate[position].name
                viewHolder.contents.text = getMyCondateContents(myCondate[position].id)

                return view
            }

            fun add(newMyCondate: MyCondate) {
                myCondate.add(newMyCondate)
            }


            fun getMyCondateContents(myCondateID: Int) : String {
                var contents = ""

                val db = when (context) {
                    null -> throw NullPointerException()
                    else -> SampleDBOpenHelper(context!!)
                }
                val myCondate_foodsTable = DBContract.MyCondate_Foods
                val foodTable = DBContract.Food

                val result: List<String> = db.searchRecord(
                    tableName   = foodTable.TABLE_NAME,
                    column      = arrayOf(
                        foodTable.NAME,
                        myCondate_foodsTable.NUMBER),
                    condition   = "${myCondate_foodsTable.MYCONDATE_ID} = ${myCondateID}",
                    innerJoin   = Join(
                        tablename   = myCondate_foodsTable.TABLE_NAME,
                        column1     = foodTable.ID,
                        column2     = myCondate_foodsTable.FOOD_ID
                    )
                ) ?: throw SQLiteException("searchRecord was failed.")
                if (result.isNotEmpty()) {
                    var i = 0
                    contents += "${result[i++]}[${result[i++]}人前]"
                    while (i < result.size) {
                        contents += ", ${result[i++]}[${result[i++]}人前]"
                    }
                }

                return contents
            }
        }
        class MyCondateDialog : DialogFragment() {
            val myCondate: MutableList<MyCondate> = mutableListOf() // 表示されているMy献立のデータ
            lateinit var adapter: MyCondateAdapter

            // ----- 基本設定 -----
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                var tmpRadio: RadioButton? = null   // 選択アイテムの表示を保持
                var checking: Int = -1              // 選択アイテムを保持
                searchMyCondate()

                val builder = AlertDialog.Builder(activity)
                builder
                    .setTitle("My献立を選択してください")
                    .setSingleChoiceItems(adapter, checking) { dialog, position ->

                        checking = position

                        val d = dialog as AlertDialog
                        val v = d.listView.getChildAt(position)
                        val radio = v.findViewById<RadioButton>(R.id.radioButton)
                        radio.isChecked = true
                        if (tmpRadio != radio) {
                            tmpRadio?.isChecked = false
                            tmpRadio = radio
                        }

                    }
                    .setPositiveButton("OK") { _, _ ->
                        if (checking < 0) {
                            Toast.makeText(context, "選択してください", Toast.LENGTH_SHORT).show()
                        } else {
                            setMyCondate(position = checking)
                        }
                    }
                    .setNegativeButton("キャンセル", null)

                return builder.create()
            }

            // ----- データベースからMy献立を検索 -----
            private fun searchMyCondate() {
                val db = when (context) {
                    null -> throw NullPointerException()
                    else -> SampleDBOpenHelper(context!!)
                }
                val myCondateTable = DBContract.MyCondate

                // 検索
                val result = db.searchRecord(
                    tableName = myCondateTable.TABLE_NAME,
                    column = arrayOf(
                        myCondateTable.ID,
                        myCondateTable.NAME
                    )
                ) ?: throw NullPointerException()

                // 格納
                var i = 0
                while (i < result.size) {
                    myCondate.add(MyCondate(
                        id      = result[i++].toInt(),
                        name    = result[i++])
                    )
                }
                for (j in 0..myCondate.size.minus(1)) {
                    adapter.add(myCondate[j])
                }
            }

            override fun onAttach(context: Context?) {
                super.onAttach(context)
                adapter = when (context) {
                    null -> throw NullPointerException()
                    else -> MyCondateAdapter(context)
                }
            }


            // ----- My献立を仮登録状況にセットする -----
            private fun setMyCondate(position: Int) {

                val myCondate = myCondate[position]                         // 仮登録するMy献立
                val foods: MutableList<TempRegistrationStateAdapter.Food> = mutableListOf() // 仮登録するMy献立の内容品目

                // 該当My献立の内容品目を検索
                val db = when (context) {
                    null -> throw NullPointerException()
                    else -> SampleDBOpenHelper(context!!)
                }
                val myCondate_foodsTable = DBContract.MyCondate_Foods
                val foodTable = DBContract.Food

                val result: List<String> = db.searchRecord(
                    tableName   = foodTable.TABLE_NAME,
                    column      = arrayOf(
                        foodTable.ID,
                        foodTable.NAME,
                        myCondate_foodsTable.NUMBER),
                    condition   = "${myCondate_foodsTable.MYCONDATE_ID} = ${myCondate.id}",
                    innerJoin   = Join(
                        tablename   = myCondate_foodsTable.TABLE_NAME,
                        column1     = foodTable.ID,
                        column2     = myCondate_foodsTable.FOOD_ID
                    )
                ) ?: throw SQLiteException("searchRecord was failed.")
                var i = 0
                while (i < result.size) {
                    foods.add(TempRegistrationStateAdapter.Food(
                        id      = result[i++].toInt(),
                        name    = result[i++],
                        number  = result[i++].toFloat())
                    )
                }

                // 仮登録状況にセット
                state.clear()
                state.addAll(foods)
                state.notifyDataSetChanged()
            }
        }

        // --------------------------------------------------
        //  ダイアログの表示
        // --------------------------------------------------
        val dialog = MyCondateDialog()
        dialog.show(fragmentManager, "setting_dialog")
    }

}
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

class CondateRegistrationFragment(): Fragment() {

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
    private val timeRadioButtonId: Map<Int?, Int> = mapOf(
        null to -1,
        0 to R.id.morningRadioButton,
        1 to R.id.noonRadioButton,
        2 to R.id.eveningRadioButton,
        3 to R.id.snackRadioButton
    )
    private val favoriteButton = listOf(
        android.R.drawable.btn_star_big_off,
        android.R.drawable.btn_star_big_on
    )

    private val selectedDay = Day()                     // 日時
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
            args.putInt("year",   year)
            args.putInt("month",  month)
            args.putInt("date",   date)
            args.putInt("time",   time)
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
                selectedDay.set(
                    args.getInt("year"),
                    args.getInt("month"),
                    args.getInt("date"),
                    args.getInt("time")
                )
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
        day.apply {
            text = when (selectedDay.contains(null)) {
                true    -> "選択してください"
                false   -> "%d/%d/%d".format(
                    selectedDay.year,
                    selectedDay.month,
                    selectedDay.date
                )
            }
            setOnClickListener {
                showDatePickerDialog(day)
            }
        }

        // ------------------------------------------------------------
        //  時間帯
        // ------------------------------------------------------------
        val time: RadioGroup = v.findViewById(R.id.timeRadioGroup)
        time.apply {
            check(timeRadioButtonId[selectedDay.time] ?: throw AssertionError())
            setOnCheckedChangeListener { _, checkedButtonId ->

                // 選択された日時をセット
                val inputDay = Day().apply {
                    set(
                        selectedDay.year,
                        selectedDay.month,
                        selectedDay.date,
                        timeRadioButtonId.filterValues { it == checkedButtonId }.keys.first() ?: throw AssertionError()
                    )
                }
                selectedDay.set(time = inputDay.time)

                // 選択された日時に献立が登録されていた場合、仮登録品目上書き確認ダイアログを表示して上書き
                val searchResult = state.searchFoods(inputDay)
                if (searchResult.isNotEmpty()) {
                    AlertDialog.Builder(context).apply {
                        setMessage("選択された日時には既に献立が登録されています。仮登録品目を上書きしました。")
                        show()
                    }
                    state.clear()
                    state.addAll(searchResult)
                }
            }
        }

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
                }
            }
        }

        // ------------------------------------------------------------
        //  My献立ボタン
        // ------------------------------------------------------------
        val myCondate: Button = v.findViewById(R.id.myCondateButton)
        myCondate.apply {
            setOnClickListener {
                showMyCondateDialog(v)
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
                when (registerCondate()) {
                    OK -> {
                        fragmentManager?.popBackStack() ?: throw NullPointerException()
                        Toast.makeText(context, "登録しました", Toast.LENGTH_SHORT).show()
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
    //  Day
    //  - 日時を管理するクラス
    //
    // ==================================================
    // ==================================================
    private inner class Day {
        var year    : Int? = null
        var month   : Int? = null
        var date    : Int? = null
        var time    : Int? = null

        val strTime
            get() = when (time) {
                0 -> "朝"
                1 -> "昼"
                2 -> "晩"
                3 -> "間"
                else -> throw AssertionError()
            }

        fun set(
            year    : Int? = null,
            month   : Int? = null,
            date    : Int? = null,
            time    : Int? = null
        ) {
            if (year    != null) { this.year   = year }
            if (month   != null) { this.month  = month }
            if (date    != null) { this.date   = date }
            if (time    != null) {
                if (time < 0 || 3 < time) { throw AssertionError("time == ${time} : Variable \"time\" must be from 0 to 3 in Integer.") }
                this.time   = time
            }
        }

        fun contains(any: Any?) : Boolean {
            return (year == any
                    || month    == any
                    || date     == any
                    || time     == any)
        }

        fun notContains(any: Any?) : Boolean {
            return !contains(any)
        }

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

        fun getAdapter() : TempRegistrationStateAdapter { return adapter }
        fun getItem(position: Int) : TempRegistrationStateAdapter.Food { return adapter.getItem(position) }

        fun init() {

            if (selectedDay.notContains(null)) {
                val registeredFoods: List<TempRegistrationStateAdapter.Food> = searchFoods(selectedDay)
                if (registeredFoods.isNotEmpty()) {
                    addAll(registeredFoods)
                }
            }

        }

        fun searchFoods(day: Day) : List<TempRegistrationStateAdapter.Food> {

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
                condition = "${recordT.TABLE_NAME}.${recordT.YEAR}  = ${day.year}" +
                        " and " + "${recordT.TABLE_NAME}.${recordT.MONTH} = ${day.month}" +
                        " and " + "${recordT.TABLE_NAME}.${recordT.DATE}  = ${day.date}" +
                        " and " + "${recordT.TABLE_NAME}.${recordT.TIME}  = ${day.time}",
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


    private fun showDatePickerDialog(day: TextView) {

        val today: Calendar = Calendar.getInstance()
        DatePickerDialog(
            context ?: throw NullPointerException(),
            DatePickerDialog.OnDateSetListener { _, year, month, date ->

                // 日時のセット
                selectedDay.set(year, month.inc(), date)
                day.text = "%d/%d/%d".format(
                    selectedDay.year,
                    selectedDay.month,
                    selectedDay.date)

                // 選択された日時に献立が登録されていた場合、仮登録品目上書き確認ダイアログを表示して上書き
                val searchResult = state.searchFoods(Day().apply { set(year, month.inc(), date, selectedDay.time) })
                if (searchResult.isNotEmpty()) {
                    AlertDialog.Builder(context).apply {
                        setMessage("選択された日時には既に献立が登録されています。仮登録品目を上書きしました。")
                        show()
                    }
                    state.clear()
                    state.addAll(searchResult)
                }
            },
            selectedDay.year           ?: today.get(Calendar.YEAR),
            selectedDay.month?.dec()   ?: today.get(Calendar.MONTH),
            selectedDay.date           ?: today.get(Calendar.DATE)
        ).show()

    }

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
                        viewHolder to view
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
            }
        }

        // --------------------------------------------------
        //  ダイアログの表示
        // --------------------------------------------------
        val dialog = MyCondateDialog()
        dialog.show(fragmentManager, "setting_dialog")
    }

    private fun registerCondate(): Int {

        // ----- 登録内容の確認 -----
        if (listOf(
                selectedDay.year,
                selectedDay.month,
                selectedDay.date,
                selectedDay.time).contains(null)) {
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
                    year    = selectedDay.year!!,
                    month   = selectedDay.month!!,
                    date    = selectedDay.date!!,
                    time    = selectedDay.time!!,
                    number  = food.number
                )
            )
        }

        return OK

    }

}
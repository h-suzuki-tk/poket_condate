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
import android.util.Log
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

    // 使用する画像ID
    private val timeRadioButtonId: Map<Int?, Int> = mapOf(
        null to -1,
        0 to R.id.morningRadioButton,
        1 to R.id.noonRadioButton,
        2 to R.id.eveningRadioButton,
        3 to R.id.snackRadioButton
    )
    private val favoriteButtonImg = listOf(
        android.R.drawable.btn_star_big_off,
        android.R.drawable.btn_star_big_on
    )

    // ビュー
    private lateinit var dayText               : TextView
    private lateinit var timeRadioGroup        : RadioGroup
    private lateinit var searchEditText        : EditText
    private lateinit var categorySpinner       : Spinner
    private lateinit var favoriteButton        : ImageButton
    private lateinit var resultList            : ListView
    private lateinit var myCondateButton       : Button
    private lateinit var condateEditList       : ListView
    private lateinit var registerFoodButton    : Button
    private lateinit var registerCondateButton : Button

    // アダプター
    private lateinit var categoryAdapter    : ArrayAdapter<String>
    private lateinit var searchAdapter      : FoodSearchResultAdapter
    private lateinit var condateEditAdapter : CondateEditAdapter

    // 変数
    private var day: Day // 日時

    init {
        day = Day()
    }

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



    // ==================================================
    //
    //  初期化
    //
    // ==================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ----- 引数を配列に格納 -----
        val args: Bundle? = arguments
        when {
            args != null -> {
                day.set(
                    args.getInt("year"),
                    args.getInt("month"),
                    args.getInt("date"),
                    args.getInt("time")
                )
            }
        }
    }


    // ==================================================
    //
    //  描画と制御
    //
    // ==================================================
    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // レイアウトの初期化
        return inflater.inflate(R.layout.fragment_condate_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 各ビューの初期化
        dayText               = view.findViewById(R.id.dayTextView)
        timeRadioGroup        = view.findViewById(R.id.timeRadioGroup)
        searchEditText        = view.findViewById(R.id.searchEditText)
        categorySpinner       = view.findViewById(R.id.categorySpinner)
        favoriteButton        = view.findViewById(R.id.favoriteFoodImageButton)
        resultList            = view.findViewById(R.id.foodSearchResultListView)
        myCondateButton       = view.findViewById(R.id.myCondateButton)
        condateEditList       = view.findViewById(R.id.registrationStateListView)
        registerFoodButton    = view.findViewById(R.id.registerNewFoodButton)
        registerCondateButton = view.findViewById(R.id.registerButton)

        // アダプターの初期化
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
        condateEditAdapter = CondateEditAdapter(requireContext()).apply {
            if (!day.containsEmpty()) { reset(day) }
        }

        // 各ビューの設定
        dayText.apply {
            text = when (day.containsEmpty()) {
                true    -> "選択してください"
                false   -> "%d/%d/%d".format(day.year, day.month, day.date)
            }
            setOnClickListener {
                showDatePickerDialog(dayText)
            }
        }

        timeRadioGroup.apply {
            if (!day.containsEmpty()) { check(timeRadioButtonId[day.time] ?: throw AssertionError()) }
            setOnCheckedChangeListener { _, checkedButtonId ->

                // 選択された日時をセット
                val selectedTime = timeRadioButtonId.filterValues { it == checkedButtonId }.keys.first() ?: throw AssertionError()
                day.set(time = selectedTime)

                // 選択された日時に献立が登録されていた場合、仮登録品目上書き確認ダイアログを表示して上書き
                condateEditAdapter.reset(day)
            }
        }

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

        categorySpinner.apply {
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

        favoriteButton.apply {
            setOnClickListener {
                when (searchAdapter.condition.favorite) {
                    ON  -> searchAdapter.updateCondition(favorite = OFF)
                    OFF -> searchAdapter.updateCondition(favorite = ON)
                }
                setImageResource(favoriteButtonImg[searchAdapter.condition.favorite])
                searchAdapter.updateResult()
            }
        }

        val fm = FoodManager()
        resultList.apply {
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
            setOnItemLongClickListener { parent, view, position, id ->
                val food = searchAdapter.getItem(position)

                // 品目削除確認ダイアログを表示
                AlertDialog.Builder(context).apply {
                    setMessage("品目「%s」を削除しますか？".format(food.name))
                    setNegativeButton("キャンセル", null)
                    setPositiveButton("削除") { _, _ ->

                        val msg = when (fm.delete(context, food.id)) {
                            OK -> "削除しました"
                            NG -> "!! 失敗しました !!\n管理者にお問い合わせください"
                            else -> throw AssertionError()
                        }
                        searchAdapter.updateResult()
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

                    }
                    show()
                }
                true

            }
        }

        myCondateButton.apply {
            setOnClickListener {
                showMyCondateDialog(view)
            }
        }

        condateEditList.apply {
            adapter = condateEditAdapter
            setOnItemClickListener { parent, view, position, id ->
                val item  = condateEditAdapter.getItem(position)

                when (id.toInt()) {

                    // ×ボタンがタップされた場合
                    /*
                     * 確認のダイアログを表示して、OKされれば削除する
                     */
                    R.id.removeImageButton -> {
                        AlertDialog.Builder(context).apply {
                            setMessage("${item.name}[${item.number}人前]を仮登録状況から削除します。よろしいですか？")
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

        registerFoodButton.apply {
            setOnClickListener {
                val foodRegistrationFragment = FoodRegistrationFragment()
                val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
                ft.addToBackStack(null)
                ft.replace(R.id.frame_contents, foodRegistrationFragment)
                ft.commit()
            }
        }

        registerCondateButton.apply {
            setOnClickListener {
                when (registerCondate()) {
                    OK -> {
                        fragmentManager?.popBackStack() ?: throw NullPointerException()
                        Toast.makeText(context, "登録しました", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }



    // ------------------------------------------------------------
    //  showDatePickerDialog
    //  - 日付選択ダイアログを表示
    // ------------------------------------------------------------
    private fun showDatePickerDialog(dayText: TextView) {

        val today: Calendar = Calendar.getInstance()
        DatePickerDialog(
            context ?: throw NullPointerException(),
            DatePickerDialog.OnDateSetListener { _, year, month, date ->

                // 日時のセット
                day.set(year, month+1, date)
                dayText.text = "%d/%d/%d".format(day.year, day.month, day.date)

                // 選択された日時に献立が登録されていた場合、仮登録品目上書き確認ダイアログを表示して上書き
                condateEditAdapter.reset(day)
            },
            if (day.year  != Day.DEFAULT) { day.year }  else { today.get(Calendar.YEAR) },
            if (day.month != Day.DEFAULT) { day.month-1 } else { today.get(Calendar.MONTH) },
            if (day.date  != Day.DEFAULT) { day.date }  else { today.get(Calendar.DATE) }
        ).show()

    }
    // ------------------------------------------------------------



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



    // ------------------------------------------------------------
    //  showMyCondateDialog
    //  - My献立選択ダイアログを表示
    // ------------------------------------------------------------
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
                    .setNegativeButton("キャン\nセル", null)
                    .setNeutralButton("現在の仮登録品目を\nMy献立に追加") { _, _ ->

                        if (condateEditAdapter.isEmpty) {
                            Toast.makeText(context, "!! 仮登録品目がありません !!", Toast.LENGTH_LONG).show()
                        } else {
                            // My献立名の入力ダイアログを表示
                            val editText: EditText = EditText(context).apply { hint = "入力してください" }
                            AlertDialog.Builder(context).apply {
                                setMessage("新しいMy献立の名前を入力してください")
                                setView(editText)
                                setPositiveButton("登録") { _, _ ->

                                    val myCondateName = editText.text.toString()

                                    if (myCondateName.isEmpty() || myCondateName.isBlank()) {
                                        Toast.makeText(context, "空白以外の文字を入力してください", Toast.LENGTH_LONG).show()
                                    } else {

                                        val foods: MutableList<Pair<Int, Float>> = mutableListOf()
                                        for (i in 0 until condateEditAdapter.count) {
                                            val food = condateEditAdapter.getItem(i)
                                            foods.add(Pair(food.id, food.number))
                                        }
                                        when (registerMyCondate(myCondateName, foods)) {
                                            OK -> Toast.makeText(context, "追加しました", Toast.LENGTH_SHORT).show()
                                            NG -> Toast.makeText(context, "!! 失敗しました !!\n管理者にお問い合わせください", Toast.LENGTH_LONG).show()
                                            else -> throw AssertionError()
                                        }

                                    }


                                }
                                setNegativeButton("キャンセル", null)
                                show()
                            }
                        }



                    }

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
                for (j in 0 until myCondate.size) {
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

                val myCondate = myCondate[position]              // 仮登録するMy献立
                val foods: MutableList<CondateEditAdapter.Food> = mutableListOf() // 仮登録するMy献立の内容品目

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
                    foods.add(CondateEditAdapter.Food(
                        id      = result[i++].toInt(),
                        name    = result[i++],
                        number  = result[i++].toFloat())
                    )
                }

                // 仮登録状況にセット
                condateEditAdapter.clear()
                condateEditAdapter.addAll(foods)
                condateEditAdapter.notifyDataSetChanged()
            }

        }

        // --------------------------------------------------
        //  ダイアログの表示
        // --------------------------------------------------
        val dialog = MyCondateDialog()
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
    private fun registerMyCondate(name: String, foods: List<Pair<Int, Float>>): Int {

        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }
        val myCondateT = DBContract.MyCondate

        // My献立を追加
        db.insertRecord(Table.MyCondate(name = name))

        // 追加したMy献立のIDを取得
        val myCondateId = db.searchRecord(
            tableName = myCondateT.TABLE_NAME,
            column = arrayOf(myCondateT.ID)
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



    // ------------------------------------------------------------
    //  registerCondate
    //  - 仮登録した品目を献立として登録する
    // ------------------------------------------------------------
    private fun registerCondate(): Int {

        // ----- 登録内容の確認 -----
        if (day.containsEmpty()) {
            Toast.makeText(context, "日時を指定してください", Toast.LENGTH_SHORT).show()
            return NG
        }
        if (condateEditAdapter.count == 0) {
            Toast.makeText(context, "１つ以上の品目を登録してください", Toast.LENGTH_SHORT).show()
            return NG
        }

        // ----- データベースを更新 -----
        condateEditAdapter.update(day)

        return OK

    }
    // ------------------------------------------------------------

}
package com.example.wse2019

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlin.NullPointerException

class CondateRegistrationFragment(): Fragment() {

    enum class Day(val str: String) {
        YEAR    ("year"),
        MONTH   ("month"),
        DATE    ("date"),
        TIME    ("time")
    }

    var day: MutableMap<Day, Int> = mutableMapOf()
    val timeRadioButtonID: List<Int> = listOf<Int>(
        R.id.morningRadioButton,
        R.id.noonRadioButton,
        R.id.eveningRadioButton,
        R.id.snackRadioButton)

    private var category            = mutableListOf<String>()   // カテゴリ一覧を格納
    private var flag_favoriteSearch = false                     // お気に入り絞り込み検索のON, OFF
    private var state               = mutableListOf<String>()   // 品目の登録状況を保持



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
        // ----- テスト用仮データ -----
        this.category = mutableListOf("カテゴリ1", "カテゴリ2", "カテゴリ3")
        this.state    = mutableListOf("サンプル1[1人前]", "サンプル2[2人前]")  // 仮

    }


    // ------------------------------------------------------------
    //  描画
    // ------------------------------------------------------------
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
            Toast.makeText(context, "日付をクリックしました", Toast.LENGTH_SHORT).show()

            /***** ここに記述 *****/

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
            val time: Int = this.timeRadioButtonID.indexOf(checkedId)
            Toast.makeText(context, "時間帯「${time}」をクリックしました", Toast.LENGTH_SHORT).show()

            /***** ここに記述 *****/

        }

        // ------------------------------------------------------------
        //  検索フィールド
        // ------------------------------------------------------------
        val foodSearchView: SearchView = v.findViewById(R.id.foodSearchView)
        foodSearchView.setIconifiedByDefault(false)
        foodSearchView.queryHint = "品目名"
        foodSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(context, "クエリー「${query}」が入力されました", Toast.LENGTH_SHORT).show()

                /***** ここに記述 *****/

                return false
            }
            override fun onQueryTextChange(query: String?): Boolean {

                /*
                入力ごとに実行される処理
                余裕があれば
                 */

                return true
            }
        })

        // ------------------------------------------------------------
        //  カテゴリ選択
        // ------------------------------------------------------------
        val category: Spinner   = v.findViewById(R.id.categorySpinner)
        val adapter             = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        category.adapter = adapter
        adapter.add("カテゴリ") // ヒントであると同時に "未選択 (全カテゴリ)" を意味
        adapter.addAll(this.category)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val str: String = when (position) {
                    0       -> "全カテゴリ"
                    else    -> "カテゴリ${position}"
                }
                Toast.makeText(context, "${str}が選択されました", Toast.LENGTH_SHORT).show()

                /***** ここに記述 *****/

            }
            override fun onNothingSelected(pstrmy: AdapterView<*>?) {
                // なにもしない
            }
        }

        // ------------------------------------------------------------
        //  お気に入り絞り込みボタン
        // ------------------------------------------------------------
        val favorite: ImageButton = v.findViewById(R.id.favoriteFoodImageButton)
        favorite.setOnClickListener { when (this.flag_favoriteSearch) {
            true -> {
                Toast.makeText(context, "お気に入りにによる絞り込みを OFF にします", Toast.LENGTH_SHORT).show()
                this.flag_favoriteSearch = false
                favorite.setImageResource(android.R.drawable.btn_star_big_off)

                /***** ここに記述 *****/

            }
            false -> {
                Toast.makeText(context, "お気に入りにによる絞り込みを ON にします", Toast.LENGTH_SHORT).show()
                this.flag_favoriteSearch = true
                favorite.setImageResource(android.R.drawable.btn_star_big_on)

                /***** ここに記述 *****/

            }
        }}

        // ------------------------------------------------------------
        //  検索結果
        // ------------------------------------------------------------
        val result: ListView    = v.findViewById(R.id.foodSearchResultListView)
        val resultAdapter       = when (context) {
            null -> throw NullPointerException()
            else -> FoodSearchResultAdapter(context!!)
        }
        result.adapter = resultAdapter
        result.setOnItemClickListener { parent, view, position, id ->
            val food: FoodSearchResultAdapter.Food = resultAdapter.getItem(position)
            when (id.toInt()) {
                R.id.favoriteButton -> {
                    Toast.makeText(context, "お気に入り${food.favorite}ボタンを押しました", Toast.LENGTH_SHORT).show()
                    resultAdapter.switchFavorite(position)

                    /***** ここに記述 *****/

                }
                R.id.addButton -> {
                    Toast.makeText(context, "${food.name}を追加します", Toast.LENGTH_SHORT).show()

                    /***** ここに記述 *****/

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
        val stateAdapter    = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1)
        state.adapter = stateAdapter
        stateAdapter.addAll(this.state)


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
    }
    override fun onDetach() {
        super.onDetach()
    }

}
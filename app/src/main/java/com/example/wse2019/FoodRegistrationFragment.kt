package com.example.wse2019


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.text.Editable
import android.text.Html
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import com.example.sample.createFoodIngredientsTable
import org.w3c.dom.Text
import java.io.File
import java.lang.ClassCastException
import kotlin.Float.Companion.NaN

const val OK = 1
const val NG = 0

class FoodRegistrationFragment() : Fragment() {

    private lateinit var ingredient : Ingredient    // 材料検索管理用クラス
    private lateinit var state      : State         // 仮の材料情報管理用クラス

    companion object {
        fun newInstance(): FoodRegistrationFragment {
            val fragment = FoodRegistrationFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ingredient.init()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_food_registration, container, false)

        // --------------------------------------------------
        //  登録方法選択による表示ビューの制御
        // --------------------------------------------------
        val radioGroup      : RadioGroup    = v.findViewById(R.id.ffr_radioGroup)
        val ingredientView  : LinearLayout  = v.findViewById(R.id.ffr_ingredientLayout)
        val nutritionView   : LinearLayout  = v.findViewById(R.id.ffr_nutritionLayout)
        val unselectedView  : TextView      = v.findViewById(R.id.ffr_unselectedTextView)
        radioGroup.apply {
            setOnCheckedChangeListener { _, checkedId ->
                unselectedView.visibility = View.GONE
                when (checkedId) {
                    R.id.ffr_ingredientRadioButton -> {
                        ingredientView  .visibility = View.VISIBLE
                        nutritionView   .visibility = View.GONE
                    }
                    R.id.ffr_nutritionRadioButton -> {
                        ingredientView  .visibility = View.GONE
                        nutritionView   .visibility = View.VISIBLE
                    }
                    else -> throw AssertionError()
                }
            }
        }


        // --------------------------------------------------
        //  品目名
        // --------------------------------------------------
        val name: EditText = v.findViewById(R.id.ffr_foodNameEditText)
        name.apply {
            hint = "入力してください"
        }


        // --------------------------------------------------
        //  何人前
        // --------------------------------------------------
        val number: EditText = v.findViewById(R.id.ffr_numberEditText)


        // --------------------------------------------------
        //  カテゴリ
        // --------------------------------------------------
        val category: Spinner   = v.findViewById(R.id.ffr_categorySpinner)
        val categoryAdapter     = when (context) {
            null -> throw NullPointerException()
            else -> ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1)}
        val cm = CategoryManager()
        categoryAdapter.apply {
            add("カテゴリ")
            addAll(cm.getCategories(context))
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        category.apply {
            adapter = categoryAdapter
        }


        // --------------------------------------------------
        //  材料選択
        // --------------------------------------------------
        // 検索ボックス
        val searchText  : EditText = v.findViewById(R.id.ffr_searchEditText)
        searchText.apply {
            hint = "材料名"
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(text: Editable?) {
                    ingredient.clear()
                    ingredient.addAll(ingredient.search(text.toString()))
                }

                override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) { /* なにもしない */ }
                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) { /* なにもしない */ }
            })
        }

        // 検索結果
        val listView    : ListView = v.findViewById(R.id.ffr_listView)
        listView.apply {
            adapter = ingredient.getAdapter()
            setOnItemClickListener { parent, view, position, id ->
                val ingredient = ingredient.getItem(position)
                when (id.toInt()) {
                    // "＋" ボタンを押した場合
                    R.id.isrr_addButton -> {

                        // ビューの設定
                        val v: View = inflater.inflate(R.layout.add_ingredient_dialog, null, false)
                        val aid_number: EditText = v.findViewById(R.id.aid_number)
                        val aid_unit: TextView = v.findViewById(R.id.aid_unit)
                        aid_number.apply {
                            hint = "入力してください"
                        }
                        aid_unit.apply {
                            text = ingredient.unit
                        }

                        AlertDialog.Builder(context).apply {
                            setMessage("量を入力してください")
                            setView(v)
                            setPositiveButton("追加") { _, _ ->

                                if (aid_number.text.isEmpty()) {
                                    Toast.makeText(context, "!! 入力してください !!", Toast.LENGTH_LONG).show()
                                } else {
                                    val num: Float = aid_number.text.toString().toFloat()
                                    if (state.add(TempIngredientStateAdapter.Ingredient(
                                            ingredient.id,
                                            ingredient.name,
                                            num,
                                            ingredient.unit
                                        )) == OK
                                    ) {
                                        Toast.makeText(context, "${ingredient.name}を ${num}${ingredient.unit} 追加しました。", Toast.LENGTH_SHORT).show()
                                        state.applyChanges()
                                    }
                                }

                            }
                            setNegativeButton("キャンセル", null)
                            show()
                        }


                    }
                }
            }
        }

        // 仮の材料情報
        val stateListView: ListView = v.findViewById(R.id.ffr_state)
        stateListView.apply {
            adapter = state.getAdapter()
            setOnItemClickListener { parent, view, position, id ->
                val ingredient = state.getItem(position)
                when (id.toInt()) {
                    // --------------------------------------------------
                    //  削除ボタンが押された場合
                    // --------------------------------------------------
                    R.id.tisr_removeButton -> {
                        // 確認ダイアログを表示
                        AlertDialog.Builder(context).apply {
                            setMessage("${ingredient.name} ${ingredient.number}${ingredient.unit} を削除します。よろしいですか？")
                            setPositiveButton("OK") { _, _ ->
                                // 削除
                                state.remove(position)
                                state.applyChanges()
                            }
                            setNegativeButton("キャンセル", null)
                            show()
                        }
                    }
                }
            }
        }


        // --------------------------------------------------
        //  栄養選択
        // --------------------------------------------------
        val sugar   : EditText = v.findViewById(R.id.ffr_sugar)
        val fat     : EditText = v.findViewById(R.id.ffr_fat)
        val protein : EditText = v.findViewById(R.id.ffr_protein)
        val vitamin : EditText = v.findViewById(R.id.ffr_vitamin)
        val mineral : EditText = v.findViewById(R.id.ffr_mineral)
        val fiber   : EditText = v.findViewById(R.id.ffr_fiber)
        val calorie : EditText = v.findViewById(R.id.ffr_calorie)

        // --------------------------------------------------
        //  メモ
        // --------------------------------------------------
        val memo: EditText = v.findViewById(R.id.ffr_memoEditText)
        memo.apply {
            hint = "コメント・レシピなどあれば入力してください"
        }


        // --------------------------------------------------
        //  登録ボタン
        // --------------------------------------------------
        val register: Button = v.findViewById(R.id.ffr_registerButton)
        var favorite = 0
        register.apply {
            setOnClickListener {
                // 確認ダイアログを表示
                AlertDialog.Builder(context).apply {
                    setTitle("この内容で登録します")
                    setSingleChoiceItems(arrayOf(
                        "お気に入り品目にしない",
                        "お気に入り品目にする"), 0) { _, position ->
                        favorite = position
                    }
                    setPositiveButton("登録") { _, _ ->
                        if (test_registerNewFood(
                            name        = name.text.toString(),
                            number      = if(number.text.isEmpty()) { NaN } else { number.text.toString().toFloat() },
                            category    = category.selectedItemPosition,
                            memo        = memo.text.toString(),
                            favorite    = favorite,
                            method      = radioGroup.checkedRadioButtonId,
                            ingredients = state.getAllItems(),
                            nutrition   = Nutrition("",
                                sugar   = if(sugar  .text.isEmpty()) { NaN } else { sugar   .text.toString().toFloat() },
                                fat     = if(fat    .text.isEmpty()) { NaN } else { fat     .text.toString().toFloat() },
                                protein = if(protein.text.isEmpty()) { NaN } else { protein .text.toString().toFloat() },
                                vitamin = if(vitamin.text.isEmpty()) { NaN } else { vitamin .text.toString().toFloat() },
                                mineral = if(mineral.text.isEmpty()) { NaN } else { mineral .text.toString().toFloat() },
                                fiber   = if(fiber  .text.isEmpty()) { NaN } else { fiber   .text.toString().toFloat() },
                                calorie = if(calorie.text.isEmpty()) { NaN } else { calorie .text.toString().toFloat() })
                        ) == OK) {
                            Toast.makeText(context, "登録しました", Toast.LENGTH_SHORT).show()
                            fragmentManager!!.popBackStack() // フラグメントを閉じる
                        }
                    }
                    setNegativeButton("キャンセル", null)
                    show()
                }

            }
        }


        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        ingredient  = Ingredient()
        state       = State()
    }
    override fun onDetach() {
        super.onDetach()
    }

    // ==================================================
    // ==================================================
    //
    //  Ingredient
    //  - 材料検索を管理するクラス
    //
    // ==================================================
    // ==================================================
    private inner class Ingredient() {
        private val adapter: IngredientSearchResultAdapter = when (context) {
            null -> throw NullPointerException()
            else -> IngredientSearchResultAdapter(context!!)
        }
        fun getAdapter() : IngredientSearchResultAdapter { return adapter }
        fun getItem(position: Int) : IngredientSearchResultAdapter.Ingredient { return adapter.getItem(position) }

        fun init() {
            val ingredients: List<IngredientSearchResultAdapter.Ingredient> = search()
            addAll(ingredients)
        }

        fun search(name: String = "") : List<IngredientSearchResultAdapter.Ingredient> {
            val ingredients: MutableList<IngredientSearchResultAdapter.Ingredient> = mutableListOf()

            val db = SampleDBOpenHelper(context)
            val ingredientT = DBContract.Ingredient // Ingredient Table
            var condition = ""

            if (name.isNotBlank() && name.isNotEmpty()) {
                condition += "${ingredientT.NAME} like '%${name}%'"
            }

            val result: List<String> = db.searchRecord(
                tableName   = ingredientT.TABLE_NAME,
                column      = arrayOf(
                    ingredientT.ID,
                    ingredientT.NAME,
                    ingredientT.UNIT
                ),
                condition = when (condition.isEmpty() || condition.isBlank()) {
                    true    -> "${ingredientT.CLASS} = 0"
                    false   -> condition + " and ${ingredientT.CLASS} = 0"
                }
            ) ?: throw NullPointerException("searchRecord was failed.")

            var i = 0
            while (i < result.size) {
                ingredients.add(IngredientSearchResultAdapter.Ingredient(
                    id      = result[i++].toInt(),
                    name    = result[i++],
                    unit    = result[i++]
                ))
            }

            return ingredients
        }

        fun clear() {
            adapter.ingredients.clear()
        }

        fun addAll(ingredients: List<IngredientSearchResultAdapter.Ingredient>) {
            adapter.ingredients.addAll(ingredients)
            adapter.notifyDataSetChanged()
        }

    }



    // ==================================================
    // ==================================================
    //
    //  State
    //  - 仮の材料情報を管理するクラス
    //
    // ==================================================
    // ==================================================
    private inner class State {
        private val adapter: TempIngredientStateAdapter = when (context) {
            null -> throw NullPointerException()
            else -> TempIngredientStateAdapter(context!!)
        }
        fun getAdapter() : TempIngredientStateAdapter { return adapter }
        fun getItem(position: Int) : TempIngredientStateAdapter.Ingredient { return adapter.getItem(position) }
        fun getAllItems() : List<TempIngredientStateAdapter.Ingredient> { return adapter.ingredients }

        fun add(ingredient: TempIngredientStateAdapter.Ingredient): Int {

            // --------------------------------------------------
            //  引数に問題がないか確認
            // --------------------------------------------------
            // 既に追加されている材料か確認
            adapter.ingredients.forEach {
                if (ingredient.id.equals(it.id)) {
                    Toast.makeText(context,
                        "！既に追加されている材料です！\nいったん削除してからお試しください。", Toast.LENGTH_LONG).show()
                    return NG
                }
            }

            // 入力された量が正しいか確認
            val number: Float = ingredient.number
            if (number.equals(NaN) || number.equals(0.0f)) {
                Toast.makeText(context,
                    "！0 ${ingredient.unit}より大きい量を入力してください！", Toast.LENGTH_LONG).show()
                return NG
            }

            // --------------------------------------------------
            //  追加
            // --------------------------------------------------
            adapter.ingredients.add(ingredient)
            return OK
        }

        fun remove(position: Int) {
            adapter.ingredients.removeAt(position)
        }

        fun applyChanges() {
            adapter.notifyDataSetChanged()
        }
    }
    class TempIngredientStateAdapter(val context: Context) : BaseAdapter() {

        // 材料情報
        data class Ingredient(
            val id      : Int,          // 材料ID
            val name    : String,       // 材料名
            var number  : Float = 0.0f, // 何人前
            val unit    : String        // 単位
        )
        var ingredients: MutableList<Ingredient> = mutableListOf()

        // 各行で表示するビュー
        data class ViewHolderItem(
            var nameText        : TextView,     // 材料名
            var numberText      : TextView,     // 何人前
            val unitText        : TextView,     // 単位
            val removeButton    : ImageButton   // 削除ボタン
        )

        override fun getCount(): Int {
            return ingredients.size
        }
        override fun getItem(position: Int): Ingredient {
            return ingredients[position]
        }
        override fun getItemId(position: Int): Long {
            return ingredients[position].id.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val p = parent as ListView
            val (viewHolder, view) = when (convertView) {
                null -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.temp_ingredient_state_row, parent, false)
                    val viewHolder = ViewHolderItem(
                        nameText        = view.findViewById(R.id.tisr_nameText),
                        numberText      = view.findViewById(R.id.tisr_numberText),
                        unitText        = view.findViewById(R.id.tisr_unitText),
                        removeButton    = view.findViewById(R.id.tisr_removeButton)
                    )
                    view.tag = viewHolder
                    viewHolder to view
                }
                else -> convertView.tag as ViewHolderItem to convertView
            }

            viewHolder.apply {
                nameText    .apply {
                    text = ingredients[position].name
                }
                numberText  .apply {
                    text = ingredients[position].number.toString()
                }
                unitText    .apply {
                    text = ingredients[position].unit
                }
                removeButton.apply {
                    setOnClickListener { p.performItemClick(view, position, R.id.tisr_removeButton.toLong()) }
                }
            }

            return view
        }
    }


    // --------------------------------------------------
    //  registerNewFood - 品目を登録する
    // --------------------------------------------------
    fun registerNewFood(
        name        : String,
        number      : Float,
        category    : Int,
        memo        : String,
        favorite    : Int,
        method      : Int,
        ingredients : List<TempIngredientStateAdapter.Ingredient>,
        nutrition   : Nutrition
    ) : Int {

        // --------------------------------------------------
        //  未入力項目等がないかチェック
        // --------------------------------------------------
        var msg = ""
        if (name.isBlank() || name.isEmpty()) { msg += "品目名を入力してください。" }
        if (number.isNaN() || number == 0.0f) { msg += "何人前かを入力してください。" }
        if (category == 0) { msg += "カテゴリを選択してください。" }
        if (method == -1) { msg += "登録方法を選択してください。" }
        when (method) {
            // 材料選択の場合
            R.id.ffr_ingredientRadioButton  -> {
                if (ingredients.isEmpty()) { msg += "少なくとも1つの材料を追加してください。" }
            }
            // 栄養選択の場合
            R.id.ffr_nutritionRadioButton   -> {
                if (nutrition.contains(NaN)) { msg += "栄養素を全て記入してください。" }
            }
            else -> AssertionError()
        }
        if (msg.isNotEmpty()) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            return NG
        }

        // --------------------------------------------------
        //  登録
        // --------------------------------------------------
        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }
        val foodT               = DBContract.Food
        val ingredientsT        = DBContract.Ingredient

        // 品目テーブルにデータを追加
        db.insertRecord(
            Table.Food(
                name = name,
                memo = if(memo.isEmpty() || memo.isBlank()) { null } else { memo },
                favorite = favorite,
                category = category
            )
        )

        // 追加した品目のIDを取得
        val foodId = db.searchRecord(
            tableName = foodT.TABLE_NAME,
            column = arrayOf(foodT.ID)
        )?.max()?.toInt() ?: throw NullPointerException()

        when (method) {
            // --------------------------------------------------
            //  材料選択の場合
            // --------------------------------------------------
            R.id.ffr_ingredientRadioButton  -> {

                // 各材料について、品目材料テーブルにデータを追加
                ingredients.forEach { ingredient ->
                    db.insertRecord(
                        Table.Food_Ingredient(
                            food_id         = foodId,
                            Ingredient_id   = ingredient.id,
                            num             = ingredient.number
                        )
                    )
                }

            }
            // --------------------------------------------------
            //  栄養選択の場合
            // --------------------------------------------------
            R.id.ffr_nutritionRadioButton   -> {

                // 材料テーブルにデータを追加
                db.insertRecord(
                    Table.Ingredient(
                        name        = name,
                        unit        = "個",
                        quantity    = null, // そのうち実装
                        sugar       = nutrition.sugar   / number,
                        fat         = nutrition.fat     / number,
                        protein     = nutrition.protein / number,
                        vitamin     = nutrition.vitamin / number,
                        mineral     = nutrition.mineral / number,
                        fiber       = nutrition.fiber   / number,
                        calorie     = nutrition.calorie / number,
                        allergen    = null, // そのうち実装
                        clas        = 1    // 1: 区分 "完成品"
                    )
                )

                // 追加した材料のIDを取得
                val ingredientId = db.searchRecord(
                    tableName = ingredientsT.TABLE_NAME,
                    column = arrayOf(ingredientsT.ID)
                )?.max()?.toInt() ?: throw NullPointerException()

                // 品目材料テーブルにデータを追加
                db.insertRecord(
                    Table.Food_Ingredient(
                        food_id         = foodId,
                        Ingredient_id   = ingredientId,
                        num             = 1.0f
                    )
                )

            }
            else -> throw AssertionError()
        }

        return OK
    }
    // --------------------------------------------------

    // --------------------------------------------------
    //  test_registerNewFood - (テスト用)品目を登録する
    // --------------------------------------------------
    /*
     * @description
     *      ※テスト用
     *      　registerNewFood同様、データベースに品目を登録することに加え
     *      Insert文を表示する
     */
    fun test_registerNewFood(
        name        : String,
        number      : Float,
        category    : Int,
        memo        : String,
        favorite    : Int,
        method      : Int,
        ingredients : List<TempIngredientStateAdapter.Ingredient>,
        nutrition   : Nutrition
    ) : Int {

        val strList: MutableList<String> = mutableListOf()

        // --------------------------------------------------
        //  未入力項目等がないかチェック
        // --------------------------------------------------
        var msg = ""
        if (name.isBlank() || name.isEmpty()) { msg += "品目名を入力してください。" }
        if (number.isNaN() || number == 0.0f) { msg += "何人前かを入力してください。" }
        if (category == 0) { msg += "カテゴリを選択してください。" }
        if (method == -1) { msg += "登録方法を選択してください。" }
        when (method) {
            // 材料選択の場合
            R.id.ffr_ingredientRadioButton  -> {
                if (ingredients.isEmpty()) { msg += "少なくとも1つの材料を追加してください。" }
            }
            // 栄養選択の場合
            R.id.ffr_nutritionRadioButton   -> {
                if (nutrition.contains(NaN)) { msg += "栄養素を全て記入してください。" }
            }
            else -> AssertionError()
        }
        if (msg.isNotEmpty()) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            return NG
        }

        // --------------------------------------------------
        //  登録
        // --------------------------------------------------
        val db = when (context) {
            null -> throw NullPointerException()
            else -> SampleDBOpenHelper(context!!)
        }
        val foodT               = DBContract.Food
        val ingredientsT        = DBContract.Ingredient

        // 品目テーブルにデータを追加
        db.insertRecord(
            Table.Food(
                name        = name,
                favorite    = favorite,
                memo        = if(memo.isEmpty() || memo.isBlank()) { null } else { memo },
                category    = category
            )
        )
        val m = if (memo.isEmpty() || memo.isBlank()) {
            "null"
        } else {
            "\"$memo\""
        }
        strList.add("Table.Food(\"${name}\", ${favorite}, ${m}, ${category})")

        // 追加した品目のIDを取得
        val foodId: Int = db.searchRecord(
            tableName = foodT.TABLE_NAME,
            column = arrayOf(foodT.ID)
        )?.last()?.toInt() ?: throw NullPointerException()

        when (method) {
            // --------------------------------------------------
            //  材料選択の場合
            // --------------------------------------------------
            R.id.ffr_ingredientRadioButton  -> {

                // 各材料について、品目材料テーブルにデータを追加
                ingredients.forEach { ingredient ->
                    db.insertRecord(
                        Table.Food_Ingredient(
                            food_id         = foodId,
                            Ingredient_id   = ingredient.id,
                            num             = ingredient.number
                        )
                    )
                    strList.add("Table.Food_Ingredient(${foodId}, ${ingredient.id}, ${ingredient.number}f)")
                }

            }
            // --------------------------------------------------
            //  栄養選択の場合
            // --------------------------------------------------
            R.id.ffr_nutritionRadioButton   -> {

                // 材料テーブルにデータを追加
                db.insertRecord(
                    Table.Ingredient(
                        name        = name,
                        unit        = "個",
                        quantity    = 1f, // そのうち実装
                        sugar       = nutrition.sugar   / number,
                        fat         = nutrition.fat     / number,
                        protein     = nutrition.protein / number,
                        vitamin     = nutrition.vitamin / number,
                        mineral     = nutrition.mineral / number,
                        fiber       = nutrition.fiber   / number,
                        calorie     = nutrition.calorie / number,
                        allergen    = 0, // そのうち実装
                        clas        = 1    // 1: 区分 "完成品"
                    )
                )
                strList.add("Table.Ingredient(\"${name}\", " +
                            "${nutrition.sugar/number}f, " +
                            "${nutrition.fat/number}f, " +
                            "${nutrition.protein/number}f, " +
                            "${nutrition.vitamin/number}f, " +
                            "${nutrition.mineral/number}f, " +
                            "${nutrition.fiber/number}f, " +
                            "${nutrition.calorie/number}f, " +
                            "1f, \"個\", 0, 1)")

                // 追加した材料のIDを取得
                val ingredientId = db.searchRecord(
                    tableName = ingredientsT.TABLE_NAME,
                    column = arrayOf(ingredientsT.ID)
                )?.last()?.toInt() ?: throw NullPointerException()

                // 品目材料テーブルにデータを追加
                db.insertRecord(
                    Table.Food_Ingredient(
                        food_id         = foodId,
                        Ingredient_id   = ingredientId,
                        num             = 1.0f
                    )
                )
                strList.add("Table.Food_Ingredient(${foodId}, ${ingredientId}, 1f)")

            }
            else -> throw AssertionError()
        }

        val array = strList.toTypedArray()
        AlertDialog.Builder(context).apply {
            setItems(array, null)
            setPositiveButton("閉じる", null)
            setCancelable(false)
            show()
        }

        return OK
    }
    // --------------------------------------------------

}
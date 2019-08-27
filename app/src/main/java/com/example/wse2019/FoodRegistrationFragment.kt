package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
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
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import com.example.sample.createFoodIngredientsTable
import org.w3c.dom.Text
import java.lang.ClassCastException


class FoodRegistrationFragment() : Fragment() {

    lateinit var ingredient: Ingredient // 材料検索管理用クラス

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
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                    Toast.makeText(context, "カテゴリが選択されました", Toast.LENGTH_SHORT).show()
                }
                override fun onNothingSelected(pstrmy: AdapterView<*>?) {
                    // なにもしない
                }
            }
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
                when (id.toInt()) {
                    R.id.isrr_addButton -> {
                        Toast.makeText(context, "材料登録ボタンが押されました", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // --------------------------------------------------
        //  栄養選択
        // --------------------------------------------------
        val sugar: EditText     = v.findViewById(R.id.ffr_sugar)
        val fat: EditText       = v.findViewById(R.id.ffr_fat)
        val protein: EditText   = v.findViewById(R.id.ffr_protein)
        val vitamin: EditText   = v.findViewById(R.id.ffr_vitamin)
        val mineral: EditText   = v.findViewById(R.id.ffr_mineral)
        val fiber: EditText     = v.findViewById(R.id.ffr_fiber)
        val calorie: EditText   = v.findViewById(R.id.ffr_calorie)

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
        register.apply {
            setOnClickListener {
                Toast.makeText(context, "登録ボタンが押されました", Toast.LENGTH_SHORT).show()
            }
        }


        return v
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        ingredient = Ingredient()
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
    inner class Ingredient() {
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
                    true -> null
                    false -> condition
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


}
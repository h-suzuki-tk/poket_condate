package com.example.wse2019

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.sample.DBContract
import com.example.sample.Dictionary
import com.example.sample.Join
import com.example.sample.SampleDBOpenHelper
import org.w3c.dom.Text

const val FOOD_IMAGE_DIRECTORY = "foods"
const val NO_IMAGE_FILENAME = "0.png"

class FoodManager {
    private val imageManager = ImageManager()

    /*
     * @品目画像ファイル名 -> 品目ID.png
     * @比率 -> 4:3
     */
    fun getBitmap(context: Context, foodId: Int): Bitmap {
        return imageManager.getBitmap(context,
            directory   = FOOD_IMAGE_DIRECTORY,
            fileName    = "${foodId}.png"
        ) ?: imageManager.getBitmap(context,
            directory   = FOOD_IMAGE_DIRECTORY,
            fileName    = NO_IMAGE_FILENAME
        ) ?: throw NullPointerException("getBitmap was failed.")
    }

    // --------------------------------------------------
    //  getFoodInformationView
    //  - 品目の情報を表示する
    // --------------------------------------------------
    data class Food(
        var categoryName: String        = "",
        var foodName    : String        = "",
        var favorite    : Int           = 0,
        var nutrition   : Nutrition?    = null,
        var ingredients : MutableList<IngredientAdapter.Ingredient> = mutableListOf(),
        var memo        : String        = ""
    )
    fun getFoodInformationView(foodId: Int, context: Context, container: ViewGroup?): View {
        val food = Food()

        // --------------------------------------------------
        //  データベースから品目の情報を検索
        // --------------------------------------------------
        val db = SampleDBOpenHelper(context)
        val foodT       = DBContract.Food
        val ingT        = DBContract.Ingredient
        val foods_ingsT = DBContract.Foods_Ingredients
        val categoryT   = DBContract.Category

        var column: Array<String>
        var result: List<String>
        var i = 0

        // ----- 品目情報の検索 -----
        column = arrayOf(
            foodT.NAME,
            foodT.FAVORITE,
            foodT.MEMO,
            foodT.CATEGORY)
        result = db.searchRecord(
            tableName   = foodT.TABLE_NAME,
            column      = column,
            condition   = "${foodT.ID} = ${foodId}"
        )?: throw NullPointerException()

        // 結果の格納
        when (result.size) {
            column.size -> {
                food.foodName   = result[0]
                food.favorite   = result[1].toInt()
                food.memo       = result[2]
            }
            else -> throw AssertionError()
        }

        // ----- カテゴリの検索 -----
        val categoryId = result[3].toInt()
        column = arrayOf(categoryT.NAME)
        result = db.searchRecord(
            tableName   = categoryT.TABLE_NAME,
            column      = column,
            condition   = "${categoryT.ID} = ${categoryId}"
        ) ?: throw NullPointerException()

        // 結果の格納
        when (result.size) {
            column.size -> food.categoryName = result[0]
            else        -> throw AssertionError()
        }

        // ----- 栄養値の計算 -----
        val nh = NutritionHelper(context)
        food.nutrition = nh.getNutritions(listOf(foodId))?.first() ?: throw NullPointerException()

        // ----- 材料情報の検索 -----
        column = arrayOf(
            "${ingT.TABLE_NAME}.${ingT.NAME}",
            "${foods_ingsT.TABLE_NAME}.${foods_ingsT.NUMBER}",
            "${ingT.TABLE_NAME}.${ingT.UNIT}")
        result = db.searchRecord(
            tableName = foods_ingsT.TABLE_NAME,
            column    = column,
            condition = "${foods_ingsT.TABLE_NAME}.${foods_ingsT.FOOD_ID} = ${foodId}",
            innerJoin = Join(
                tablename   = ingT.TABLE_NAME,
                column1     = foods_ingsT.INGREDIENT_ID,
                column2     = ingT.ID
            )
        ) ?: throw NullPointerException()

        // 結果の格納
        while (i < result.size) {
            food.ingredients.add(IngredientAdapter.Ingredient(
                    name    = result[i++],
                    number  = result[i++].toFloat(),
                    unit    = result[i++]))
        }

        // --------------------------------------------------
        //  ビューの設定
        // --------------------------------------------------
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_food_information, container, false)

        val category = view.findViewById<TextView>(R.id.ffi_categoryTextView).apply { text = food.categoryName }
        val foodName = view.findViewById<TextView>(R.id.ffi_foodNameTextView).apply { text = food.foodName }
        val favorite = view.findViewById<LinearLayout>(R.id.ffi_favoriteLinearLayout)
        favorite.apply {
            visibility = when (food.favorite) {
                1 -> View.VISIBLE
                0 -> View.GONE
                else -> throw AssertionError()
            }
        }
        val calorie = view.findViewById<TextView>(R.id.ffi_calorieTextView) .apply { text = String.format("%.0f", food.nutrition!!.calorie) }
        val sugar   = view.findViewById<TextView>(R.id.ffi_sugarTextView)   .apply { text = String.format("%.1f", food.nutrition!!.sugar  ) }
        val fat     = view.findViewById<TextView>(R.id.ffi_fatTextView)     .apply { text = String.format("%.1f", food.nutrition!!.fat    ) }
        val protein = view.findViewById<TextView>(R.id.ffi_proteinTextView) .apply { text = String.format("%.1f", food.nutrition!!.protein) }
        val vitamin = view.findViewById<TextView>(R.id.ffi_vitaminTextView) .apply { text = String.format("%.1f", food.nutrition!!.vitamin) }
        val mineral = view.findViewById<TextView>(R.id.ffi_mineralTextView) .apply { text = String.format("%.1f", food.nutrition!!.mineral) }
        val fiber   = view.findViewById<TextView>(R.id.ffi_fiberTextView)   .apply { text = String.format("%.1f", food.nutrition!!.fiber  ) }

        val ingLayout = view.findViewById<LinearLayout>(R.id.ffi_ingredientLinearLayout)
        val ingredient = view.findViewById<ListView>(R.id.ffi_ingredientListView)
        val ingAdapter = IngredientAdapter(context)
        ingLayout.apply {
            visibility = if (food.ingredients.isEmpty() || food.ingredients.size == 1) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        ingredient.apply {
            adapter = ingAdapter
        }
        ingAdapter.apply {
            ingredients.addAll(food.ingredients)
        }

        val memoLayout = view.findViewById<LinearLayout>(R.id.ffi_memoLinearLayout)
        memoLayout.apply {
            visibility = if ( food.memo == "null" || food.memo.isEmpty() || food.memo.isBlank()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        val memoText = view.findViewById<TextView>(R.id.ffi_memoTextView).apply { text = food.memo }

        // --------------------------------------------------
        //  ダイアログの作成
        // --------------------------------------------------
        return view
    }
    class IngredientAdapter(val context: Context) : BaseAdapter() {

        // 材料情報
        data class Ingredient(
            val name    : String,
            val number  : Float,
            val unit    : String
        )
        var ingredients: MutableList<Ingredient> = mutableListOf()

        // 各行で表示するビュー
        data class ViewHolderItem(
            val name    : TextView,
            val number  : TextView,
            val unit    : TextView
        )

        override fun getCount(): Int {
            return ingredients.size
        }
        override fun getItem(position: Int): Ingredient {
            return ingredients[position]
        }
        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val p = parent as ListView
            val (viewHolder, view) = when (convertView) {
                null -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.ingredient_row, parent, false)
                    val viewHolder = ViewHolderItem(
                        name    = view.findViewById(R.id.ir_nameText),
                        number  = view.findViewById(R.id.ir_numberText),
                        unit    = view.findViewById(R.id.ir_unitText))
                    view.tag = viewHolder
                    viewHolder to view
                }
                else -> convertView.tag as ViewHolderItem to convertView
            }

            viewHolder.apply {
                name    .apply {
                    text = ingredients[position].name
                }
                number  .apply {
                    text = ingredients[position].number.toString()
                }
                unit    .apply {
                    text = ingredients[position].unit
                }
            }

            return view
        }
    }
    // --------------------------------------------------


}
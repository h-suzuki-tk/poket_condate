package com.example.wse2019

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.sample.*
import kotlinx.android.synthetic.main.fragment_regisration_inf_edit.view.*
import org.w3c.dom.Text

const val FOOD_IMAGE_DIRECTORY = "foods"
const val NO_IMAGE_FILENAME = "0.jpg"


class FoodManager {

    data class Food(
        var categoryName: String        = "",
        var foodName    : String        = "",
        var favorite    : Int           = 0,
        var nutrition   : Nutrition?    = null,
        var ingredients : MutableList<Ingredient> = mutableListOf(),
        var memo        : String        = ""
    )

    data class Ingredient(
        val name    : String,
        val number  : Float,
        val unit    : String,
        val clas    : Int
    )

    /*
     * @品目画像ファイル名 -> 品目ID.png
     * @比率 -> 4:3
     */
    fun getBitmap(context: Context, foodId: Int): Bitmap {
        val imageManager = ImageManager()
        return imageManager.getBitmap(context,
            directory   = FOOD_IMAGE_DIRECTORY,
            fileName    = "${foodId}.jpg"
        ) ?: imageManager.getBitmap(context,
            directory   = FOOD_IMAGE_DIRECTORY,
            fileName    = NO_IMAGE_FILENAME
        ) ?: throw NullPointerException("getBitmap was failed.")
    }

    // --------------------------------------------------
    //  getFoodInformationView
    //  - 品目の情報を表示する
    // --------------------------------------------------
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
            "${ingT.TABLE_NAME}.${ingT.UNIT}",
            "${ingT.TABLE_NAME}.${ingT.CLASS}")
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
            food.ingredients.add(Ingredient(
                    name    = result[i++],
                    number  = result[i++].toFloat(),
                    unit    = result[i++],
                    clas    = result[i++].toInt()))
        }

        // --------------------------------------------------
        //  ビューの設定
        // --------------------------------------------------
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.fragment_food_information, container, false)

        val img      = view.findViewById<ImageView>(R.id.ffi_food_img).apply {
            setImageBitmap(getBitmap(context, foodId))
        }
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
        val fiber   = view.findViewById<TextView>(R.id.ffi_fiberTextView)   .apply { text = String.format("%.1f", food.nutrition!!.fiber  ) }

        val ingLayout = view.findViewById<LinearLayout>(R.id.ffi_ingredientLinearLayout)
        val ingredient = view.findViewById<TableLayout>(R.id.ffi_ingredientListView)
        ingLayout.apply {
            visibility = if (food.ingredients.size == 1 && food.ingredients.first().clas == 1) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
        for ( i in 0..food.ingredients.size-1 ) {
            inflater.inflate(R.layout.ingredient_row, ingredient)
            ingredient.getChildAt(i).let { row ->
                (row.findViewById(R.id.ir_nameText) as TextView).apply {
                    text = food.ingredients[i].name
                }
                (row.findViewById(R.id.ir_numberText) as TextView).apply {
                    text = food.ingredients[i].number.toString()
                }
                (row.findViewById(R.id.ir_unitText) as TextView).apply {
                    text = food.ingredients[i].unit
                }
            }
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

    // ------------------------------------------------------------
    //  delete
    //  - 品目をデータベースから削除する
    // ------------------------------------------------------------
    /*
     * @params
     *      foodId: Int
     */
    fun delete(context: Context, foodId: Int): Int {
        var result: Int = NG

        val db = SampleDBOpenHelper(context)
        val foodT               = DBContract.Food
        val ingredientT         = DBContract.Ingredient
        val food_ingredientsT   = DBContract.Foods_Ingredients

        // 材料のIDと区分を取得
        val ingredients = db.searchRecord(
            tableName   = ingredientT.TABLE_NAME,
            column      = arrayOf(ingredientT.ID, ingredientT.CLASS)
        ) ?: throw NullPointerException()
        if (ingredients.isEmpty()) { throw AssertionError() }

        // 材料が完成品1つのみであった場合、その材料を削除
        /*
         * ingredients.size == 2 : 材料が1つのみであれば、ingredientsにはその材料のID, CLASSの2つしか入っていないため
         */
        val (id, clas) = Pair(ingredients[0].toInt(), ingredients[1].toInt())
        if (ingredients.size == 2 && clas == IngredientClass.FINISHED_FOOD) {
            result = when (db.deleteRecord(
                tablename       = ingredientT.TABLE_NAME,
                condition       = "${ingredientT.ID} = ?",
                selectionArgs   = arrayOf(id.toString())
            )) {
                true    -> OK
                false   -> NG
            }
            if (result == NG) { return result }
        }

        // 品目材料の削除
        result = when (db.deleteRecord(
            tablename       = food_ingredientsT.TABLE_NAME,
            condition       = "${food_ingredientsT.FOOD_ID} = ?",
            selectionArgs   = arrayOf(foodId.toString())
        )) {
            true  -> OK
            false -> NG
        }
        if (result == NG) { return result }

        // 品目の削除
        result = when (db.deleteRecord(
            tablename = foodT.TABLE_NAME,
            condition = "${foodT.ID} = ?",
            selectionArgs = arrayOf(foodId.toString())
        )) {
            true    -> OK
            false   -> NG
        }

        return result
    }
    // ------------------------------------------------------------

}
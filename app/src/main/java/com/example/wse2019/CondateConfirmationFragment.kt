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

class CondateConfirmationFragment(): Fragment() {
    private var day : DayManager
    private val fm  : FoodManager
    lateinit var foodListAdapter : FoodListAdapter

    init {
        day = DayManager()
        fm  = FoodManager()
    }

    // ------------------------------------------------------------
    //  値の取得
    // ------------------------------------------------------------
    companion object {
        fun newInstance(
            year    : Int,
            month   : Int,
            date    : Int,
            time    : Int) : CondateConfirmationFragment
        {
            val f = CondateConfirmationFragment()
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

        foodListAdapter = FoodListAdapter(requireContext()).also { adptr ->
            getRegisteredFoods(day)?.let { foods ->
                adptr.allFoods.addAll( foods )
                adptr.foodsToShow = adptr.allFoods
            }?: Toast.makeText(context, "データセットの読み込みに失敗しました", Toast.LENGTH_LONG).show()
        }
    }


    // ==================================================
    //
    //  描画と制御
    //
    // ==================================================
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_condate_confirmation, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val no_item_msg : TextView = view.findViewById(R.id.fcc_no_item_msg)
        val food_list   : ListView = view.findViewById(R.id.fcc_foods_list)
        val edit_btn    : Button   = view.findViewById(R.id.fcc_edit_btn)

        if ( foodListAdapter.allFoods.isEmpty() ) {
            food_list.visibility   = View.GONE
            no_item_msg.visibility = View.VISIBLE
        }

        food_list.run {
            adapter = foodListAdapter
            setOnItemClickListener { view, parent, position, id ->
                foodListAdapter.getItem(position).let { item ->
                    AlertDialog.Builder(context).apply {
                        setView(fm.getFoodInformationView(item.id, context, null))
                    }.show()
                }

            }
        }
        edit_btn.run {
            setOnClickListener {
                (requireActivity() as? MainActivity)?.replaceFragment(
                    CondateRegistrationFragment.newInstance(
                        day.year,
                        day.month,
                        day.date,
                        day.time)
                )
            }
        }
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }



    private fun getRegisteredFoods(day : DayManager) : MutableList<FoodListAdapter.Food>? {

        val foods: MutableList<FoodListAdapter.Food> = mutableListOf()

        val db      = SampleDBOpenHelper(context)
        val nh      = NutritionHelper(context)
        val foodT   = DBContract.Food
        val recordT = DBContract.Record

        // 検索
        db.searchRecord(
            tableName = foodT.TABLE_NAME,
            column = arrayOf(
                "${foodT.TABLE_NAME}.${foodT.ID}",
                foodT.NAME,
                foodT.FAVORITE
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
        )?.let { result ->
            if (result.isNotEmpty()) {
                var i = 0
                while ( i < result.size ) {
                    val food_id = result[i++].toInt()
                    foods.add(FoodListAdapter.Food(
                        id        = food_id,
                        name      = result[i++],
                        favorite  = result[i++].toInt(),
                        nutrition = nh.getNutritions(listOf(food_id))?.first() ?: return null))
                }
            }
        }?: return null

        return foods
    }

}
package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.example.sample.Table
import com.example.sample.createFoodIngredientsTable
import org.w3c.dom.Text
import java.lang.ClassCastException


class FoodRegistrationFragment() : Fragment() {

    companion object {
        fun newInstance(): FoodRegistrationFragment {
            val fragment = FoodRegistrationFragment()
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v: View = inflater.inflate(R.layout.fragment_food_registration, container, false)

        val ingredientView  : LinearLayout = v.findViewById(R.id.ffr_ingredientLayout)
        val nutritionView   : LinearLayout = v.findViewById(R.id.ffr_nutritionLayout)

        val register: Button = v.findViewById(R.id.ffr_registerButton)
        register.apply {
            setOnClickListener {
                val currentVisibility = ingredientView.visibility
                when (currentVisibility) {
                    View.VISIBLE -> {
                        ingredientView.visibility = View.GONE
                        nutritionView.visibility = View.VISIBLE
                    }
                    View.GONE -> {
                        ingredientView.visibility = View.VISIBLE
                        nutritionView.visibility = View.GONE
                    }
                    else -> throw AssertionError()
                }
            }
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
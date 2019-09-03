package com.example.wse2019

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class FoodInfFragment() : Fragment() {

    private var foodId=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle=arguments
        if(bundle!=null){
            foodId=bundle.getString("FOOD_ID")
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v=inflater.inflate(R.layout.fragment_evaluation, container, false)

        val textCondateName=v.findViewById<TextView>(R.id.condate_name)
        val textCalorie=v.findViewById<TextView>(R.id.calorie)
        val textProtein=v.findViewById<TextView>(R.id.protein)
        val textVitamin=v.findViewById<TextView>(R.id.vitamin)
        val textFat=v.findViewById<TextView>(R.id.fat)
        val textSugar=v.findViewById<TextView>(R.id.sugar)
        val textMineral=v.findViewById<TextView>(R.id.mineral)
        return v
    }

}
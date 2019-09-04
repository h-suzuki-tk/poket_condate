package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


class TopFragment() : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_top, container, false)

        // --------------------------------------------------
        //  今週の得点
        // --------------------------------------------------
        val score = view.findViewById<TextView>(R.id.ft_scoreTextView)
        score.apply {
            //text = /* score */
        }

        // --------------------------------------------------
        //  最もおすすめな品目
        // --------------------------------------------------
        val recommendFood = view.findViewById<LinearLayout>(R.id.ft_recommendFood)
        val image = view.findViewById<ImageView>(R.id.ft_recommendImageView)
        val name = view.findViewById<TextView>(R.id.ft_foodNameTextView)
        recommendFood.apply {
            setOnClickListener {
                // 献立登録画面へ (余裕があれば、その前に品目の詳細を表示する)
            }
        }
        image.apply {
            //setImageResource(recommendFood.first.id)
        }
        name.apply {
            //text = recommendFood.first.name
        }

        // --------------------------------------------------
        //  そのほかのおすすめ品目
        // --------------------------------------------------
        val otherRecommendFood = view.findViewById<ListView>(R.id.ft_otherRecommendFoodListView)
        otherRecommendFood.apply {
            //adapter = recommendFood.getAdapter()
            setOnItemClickListener { view, parent, position, id ->
                //献立登録画面へ (余裕があれば、その前に品目の詳細を表示する)
            }
        }

        // --------------------------------------------------
        //  もっと見る！ボタン
        // --------------------------------------------------
        val showMore = view.findViewById<LinearLayout>(R.id.ft_showMoreTextView)
        showMore.apply {
            setOnClickListener {
                showMore.visibility = View.GONE
                //recommendFood.showMore(10)
            }
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }
    override fun onDetach() {
        super.onDetach()
    }
    companion object {
        fun newInstance(): TopFragment {
            val fragment = TopFragment()
            return fragment
        }
    }
}
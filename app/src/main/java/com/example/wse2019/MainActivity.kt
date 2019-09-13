package com.example.wse2019


import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.sample.DBContract
import com.example.sample.SampleDBOpenHelper
import com.example.sample.Table
import com.example.sample.initializer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    CalendarFragment.OnCellSelectedListener,
    TabFragment.OnRegisterNewCondateSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // データベースの初期化
        initializer(this)

        // ツールバーをセット
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //ナビゲーションドロワーをセット
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val nav_view: NavigationView = findViewById(R.id.nav_view)
        val header: View = nav_view.getHeaderView(0)

        val userName: TextView = header.findViewById(R.id.nhm_userNameTextView)
        userName.apply {
            text = "こんにちは！\n${getUserName()} さん"
        }
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        //最初に表示する画面の設定
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_contents, TabFragment())
        ft.commit()
    }

    var TAG = "MainActivity"

    // ナビゲーションメニューの各項目を選択した際の動作
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment:Fragment?=null
        val arg = Bundle()

        //選択したメニューにしたがってfragmentを挿入
        when (item.itemId) {
            R.id.nav_top -> {
                fragment=TabFragment()
                arg.putInt("PageIndex", 0)
                fragment.arguments = arg
                Log.d(TAG,"Nav top Selected!")
            }
            R.id.nav_calendar -> {
                fragment=TabFragment()
                arg.putInt("PageIndex", 1)
                fragment.arguments = arg
                Log.d(TAG,"Nav calendar Selected!")
            }
            R.id.nav_evaluation -> {
                fragment=TabFragment()
                arg.putInt("PageIndex", 2)
                fragment.arguments = arg
                Log.d(TAG,"Nav evaluation Selected!")
            }
            R.id.nav_mycondate -> {
                fragment=MyCondateFragment()
                Log.d(TAG,"Nav my condate Selected!")
            }
            R.id.nav_food_registration -> {
                fragment=FoodRegistrationFragment()
                Log.d(TAG,"Nav food registration Selected!")
            }
            R.id.nav_regisration_inf_edit -> {
                fragment=RegistrationInfEditFragment()
                Log.d(TAG,"Nav registration inf edit Selected!")
            }
        }
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame_contents, fragment)
            ft.commit()
        }

        // Close the Navigation Drawer.
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }



    // --------------------------------------------------
    //  getUserName
    //  - ユーザー名を取得
    // --------------------------------------------------
    fun getUserName(): String {

        var userName = ""

        val db = SampleDBOpenHelper(this)
        val userT = DBContract.UserInfo

        userName = db.searchRecord(
            tableName = userT.TABLE_NAME,
            column = arrayOf(userT.NAME)
        )?.first() ?: throw NullPointerException()

        return userName
    }
    // --------------------------------------------------


    override fun replaceFragment(fragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.frame_contents, fragment)
        ft.commit()
    }

}

package com.example.wse2019


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
import android.widget.Button
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
        //initDB()  //初期化案1
        initializer(this)   //初期化案Ⅱ(かじむら大明神作)

        // ツールバーをセット
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //ナビゲーションドロワーをセット
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val nav_view: NavigationView = findViewById(R.id.nav_view)
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

    override fun replaceFragment(fragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.addToBackStack(null)
        ft.replace(R.id.frame_contents, fragment)
        ft.commit()
    }


    // ----------------------------------------------------------------------
    //  initDB - データベース初期化
    // ----------------------------------------------------------------------
    /*
    内容: 仮のデータをいれる
    目的: テストをしたいとき、各自で insert するのは手間であるため共有できるようにする
    編集: 自由に追加可
     */
    private fun initDB() {
        val db = SampleDBOpenHelper(this).writableDatabase
        val DB = SampleDBOpenHelper(this)

        DB.dropTables(db)
        DB.onCreate(db)

        insertIngredient(DB)
        insertFood(DB)
        insertRecord(DB)
    }

    private fun insertIngredient(DB: SampleDBOpenHelper) {
        DB.insertRecord(
            Table.Ingredient(
                "お米", 38.1f, 0.3f, 3.5f, 0f, 0f,
                0f, 168f, 100f, "グラム",0
            )
        ) // 1 (id)
        DB.insertRecord(
            Table.Ingredient(
                "いくら", 0.12f, 9.36f, 19.56f, null, 0f,
                null, 163f, 60f, "グラム",1
            )
        ) // 2
    }

    private fun insertFood(DB: SampleDBOpenHelper) {
        DB.insertRecord(
            Table.Food("いくらご飯", 0, null, 8)
        ) // 1 (id)
    }

    private fun insertRecord(DB: SampleDBOpenHelper) {
        DB.insertRecord(
            Table.Record(1, 2019, 7, 4, 1)
        ) // 1 (id)
    }
}

package com.example.wse2019


import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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


        // タブとViewPagerをセット
        val tabsFragments = arrayListOf(
            TopFragment::class.java,
            CalendarFragment::class.java,
            EvaluationFragment::class.java
        )

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val container: ViewPager = findViewById(R.id.container)

        container.adapter = TabsPagerAdapter(supportFragmentManager, tabsFragments)
        tabLayout.setupWithViewPager(container)

        //最初に表示する画面の設定
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frame_contents, TopFragment())
        ft.commit()
    }


    var TAG = "MainActivity"

    // ナビゲーションメニューの各項目を選択した際の動作
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment:Fragment?=null


        //選択したメニューにしたがってfragmentを挿入
        when (item.itemId) {
            R.id.nav_top -> {
                fragment=TopFragment()
                Log.d(TAG,"Nav top Selected!")
            }
            R.id.nav_calendar -> {
                fragment=CalendarFragment()
                Log.d(TAG,"Nav calendar Selected!")
            }
            R.id.nav_evaluation -> {
                fragment=EvaluationFragment()
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



}

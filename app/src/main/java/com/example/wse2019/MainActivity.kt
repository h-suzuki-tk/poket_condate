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
import android.widget.*
import com.example.sample.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var toolbar        : Toolbar
    private lateinit var drawerLayout   : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var header         : View
    private lateinit var userNameText   : TextView
    private lateinit var toggle         : ActionBarDrawerToggle

    private lateinit var userName : String

    // 初期化
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ツールバーをセット
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // ユーザー情報の取得
        userName = getUserName()

        // データベースの初期化
        if (userName.isBlank()) {
            Toast.makeText(
                this,
                "データベースの初期化を開始します。\nしばらくお待ちください...",
                Toast.LENGTH_LONG
            ).show()
            initializer(this)
            Toast.makeText(
                this,
                "...データベースの初期化が完了しました。\nようこそ！",
                Toast.LENGTH_LONG
            ).show()
            userName = getUserName()
        }

        // ナビゲーションドロワーのセット
        drawerLayout   = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.nav_view)
        header         = navigationView.getHeaderView(0)
        userNameText   = header.findViewById(R.id.nhm_userNameTextView)

        userNameText.apply { text = "こんにちは！\n${userName} さん" }
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        // デフォルトの画面をセット
        replaceFragment(TabFragment(), false)
    }

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
            val ft = supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_contents, fragment)
                addToBackStack(null)
            }
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
        )?.first() ?: ""

        return userName
    }
    // --------------------------------------------------


    fun replaceFragment(
        fragment       : Fragment,
        addToBackStack : Boolean = true) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_contents, fragment)
            if (addToBackStack) { addToBackStack(null) }
        }.commit()
    }

}

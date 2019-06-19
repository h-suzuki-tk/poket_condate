package com.example.wse2019

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.support.design.widget.TabLayout
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ツールバーをアクションバーとしてセット
        // -------------------------------------------------------------------------------
        var toolbar: Toolbar = findViewById(R.id.toolBar)

        toolbar.setNavigationIcon(R.mipmap.ic_launcher_round)
        toolbar.setTitle("ほげほげ")

        setSupportActionBar(toolbar)
        // -------------------------------------------------------------------------------

        // タブをセット
        // -------------------------------------------------------------------------------
        var tabLayout: TabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText(R.string.top))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.record))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.eval))
        // -------------------------------------------------------------------------------
    }

}

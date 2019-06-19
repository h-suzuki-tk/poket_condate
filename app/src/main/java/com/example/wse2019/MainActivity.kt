package com.example.wse2019

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* ツールバーをアクションバーとしてセット */
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        toolbar.setNavigationIcon(R.mipmap.ic_launcher_round)

        setSupportActionBar(toolbar)
    }

}

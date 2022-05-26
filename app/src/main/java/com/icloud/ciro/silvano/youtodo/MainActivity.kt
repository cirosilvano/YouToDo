package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    var numberOfItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var sharedPreferences=getSharedPreferences("themePref", MODE_PRIVATE)
        var value=sharedPreferences.getInt("THEME",0)
        when(value){
            0-> setTheme(R.style.Theme_YouToDo)
            1->setTheme(R.style.Theme_YouToDoWinter)
            2->setTheme(R.style.Theme_YouToDoFall)
        }
    }

}
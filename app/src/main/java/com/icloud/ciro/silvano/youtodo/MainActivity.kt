package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    var numberOfItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var sharedPrefTheme=getSharedPreferences("themePref", MODE_PRIVATE)

        var valueTheme=sharedPrefTheme.getInt("THEME",0)
        when(valueTheme){
            0-> setTheme(R.style.Theme_YouToDo)
            1->setTheme(R.style.Theme_YouToDoWinter)
            2->setTheme(R.style.Theme_YouToDoFall)
        }

        var isDarkModeOn = sharedPrefTheme.getBoolean("MODE",false)

        Log.d("","isDarkModeOn ${isDarkModeOn}")
        if(isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


    }

}
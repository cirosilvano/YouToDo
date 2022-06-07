package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //Reimpostazione dello stato rispetto all'ultima apertura dell'app.
        val sharedPrefTheme=getSharedPreferences("themePref", MODE_PRIVATE)

        when(sharedPrefTheme.getInt("THEME",0)){
            0-> setTheme(R.style.Theme_YouToDo)
            1->setTheme(R.style.Theme_YouToDoWinter)
            2->setTheme(R.style.Theme_YouToDoFall)
        }

        val isDarkModeOn = sharedPrefTheme.getBoolean("MODE",false)

        if(isDarkModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
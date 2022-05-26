package com.icloud.ciro.silvano.youtodo

import android.content.SharedPreferences
import android.media.audiofx.Equalizer
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.color.DynamicColors
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Gestione dello stato persistente per il corretto caricamento del tema impostato*/

        var sharedPreferences=getSharedPreferences("themePref", MODE_PRIVATE)
        var themeType=sharedPreferences.getInt("THEME",0)
        Log.d("","PREFERENCES: ${themeType}")
        when(themeType){
            0->setTheme(R.style.Theme_YouToDo)
            1-> setTheme(R.style.Theme_YouToDoWinter)
            2->  setTheme(R.style.Theme_YouToDoFall)
        }
        setContentView(R.layout.activity_main)


    }



}
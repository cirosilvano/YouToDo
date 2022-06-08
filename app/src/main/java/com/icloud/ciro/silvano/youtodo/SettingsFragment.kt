package com.icloud.ciro.silvano.youtodo

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icloud.ciro.silvano.youtodo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get()=_binding!!
    private var sharedPreferences:SharedPreferences?=null
    private var prefEditor:SharedPreferences.Editor?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionInflater.from(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        /* Per mantenere lo stato persistente dell'applicazione (in questo caso le preferenze relative
         * al tema dell'applicazione), utilizziamo la classe SharedPreferences.
         * Qui otteniamo l'ultimo tema che era stato impostato (null se è la prima volta che si utilizza
         * l'applicazione o non è mai stato modificato il tema)
         */
        sharedPreferences = activity?.getSharedPreferences("themePref", MODE_PRIVATE)
        prefEditor = sharedPreferences?.edit()

        //Stati dei ToggleButtons
        val btnDarkState = sharedPreferences!!.getBoolean("TOGGLE_DARK", false)
        val btnDefaultState = sharedPreferences!!.getBoolean("TOGGLE_DEFAULT", false)
        val btnTheme1State = sharedPreferences!!.getBoolean("TOGGLE_THEME1", false)
        val btnTheme2State = sharedPreferences!!.getBoolean("TOGGLE_THEME2", false)
        val bottomAppBar: BottomNavigationView = binding.bottomNavigationView

        binding.bottomNavigationView.menu.getItem(2).isChecked = true


        //Impostazione dello stato dei toggleButtons appartenenti al gruppo dei temi
        if (btnDefaultState) {
            binding.toggleButtonTheme.check(R.id.btnDefault)
        }
        if (btnTheme1State) {
            binding.toggleButtonTheme.check(R.id.btnTheme1)
        }
        if (btnTheme2State) {
            binding.toggleButtonTheme.check(R.id.btnTheme2)
        }

        //Impostazione dello stato dei toggleButtons appartenenti al gruppo delle modalità
        if (btnDarkState) {
            binding.toggleButtonMode.check(R.id.btnDarkMode)
        } else {
            binding.toggleButtonMode.check(R.id.btnLightMode)
        }

        /*
        *Gestione della selezione dei toggleButtons appartenenti al gruppo dei temi. Per ogni
        *tipo di selezione viene salvato un certo stato per i TogglesButtons del gruppo e per il tema.
        */
        binding.toggleButtonTheme.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnTheme1 -> {
                        activity?.setTheme(R.style.Theme_YouToDoWinter)
                        prefEditor?.putInt("THEME", 1)
                        prefEditor?.putBoolean("TOGGLE_THEME1", isChecked)
                        prefEditor?.putBoolean("TOGGLE_THEME2", !isChecked)
                        prefEditor?.putBoolean("TOGGLE_DEFAULT", !isChecked)
                        prefEditor?.commit()
                        activity?.recreate()

                    }
                    R.id.btnTheme2 -> {

                        activity?.setTheme(R.style.Theme_YouToDoFall)
                        prefEditor?.putInt("THEME", 2)
                        prefEditor?.putBoolean("TOGGLE_THEME1", !isChecked)
                        prefEditor?.putBoolean("TOGGLE_THEME2", isChecked)
                        prefEditor?.putBoolean("TOGGLE_DEFAULT", !isChecked)
                        prefEditor?.commit()
                        activity?.recreate()
                    }

                    R.id.btnDefault -> {
                        prefEditor?.putInt("THEME", 0)
                        prefEditor?.putBoolean("TOGGLE_THEME1", !isChecked)
                        prefEditor?.putBoolean("TOGGLE_THEME2", !isChecked)
                        prefEditor?.putBoolean("TOGGLE_DEFAULT", isChecked)
                        prefEditor?.commit()
                        activity?.recreate()
                    }
                }
            }
        }



        /*
        *Gestione della selezione dei toggleButtons appartenenti al gruppo delle modalità. Per ogni
        *tipo di selezione viene salvato un certo stato per i TogglesButtons del gruppo e per la modalità.
        */
        binding.toggleButtonMode.addOnButtonCheckedListener { _, checkedId, isChecked ->

            if (isChecked) {
                when (checkedId) {
                    R.id.btnDarkMode -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        prefEditor?.putBoolean("MODE", isChecked)
                        prefEditor?.putBoolean("TOGGLE_DARK", isChecked)
                        prefEditor?.putBoolean("TOGGLE_LIGHT", !isChecked)
                        prefEditor?.apply()
                    }
                    R.id.btnLightMode -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        prefEditor?.putBoolean("MODE", !isChecked)
                        prefEditor?.putBoolean("TOGGLE_DARK", !isChecked)
                        prefEditor?.putBoolean("TOGGLE_LIGHT", isChecked)
                        prefEditor?.apply()
                    }
                }
            }
        }

        //Gestione dei click per i vari tasti del menù

        bottomAppBar.setOnItemSelectedListener { menuItem->
            when (menuItem.itemId) {
                //Questo tasto mostra tutte le card che sono state create
                R.id.home_nav -> {
                    findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
                }

                R.id.settings_nav->{

                }

                R.id.category_nav->{
                    findNavController().navigate(R.id.action_settingsFragment_to_categoryFragment)
                }
            }
            true

        }

        return binding.root
    }
}
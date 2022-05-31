package com.icloud.ciro.silvano.youtodo

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.icloud.ciro.silvano.youtodo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get()=_binding!!
    private var sharedPreferences:SharedPreferences?=null
    private var prefEditor:SharedPreferences.Editor?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        /* Per mantenere lo stato persistente dell'applicazione (in questo caso le preferenze relative
         * al tema dell'applicazione), utilizziamo la classe SharedPreferences.
         * Qui otteniamo l'ultimo tema che era stato impostato (null se è la prima volta che si utilizza
         * l'applicazione o non è mai stato modificato il tema)
         */
        sharedPreferences = activity?.getSharedPreferences("themePref", MODE_PRIVATE)
        prefEditor = sharedPreferences?.edit()

        //Stati dei ToggleButtons
        var btnDarkState = sharedPreferences!!.getBoolean("TOGGLE_DARK", false)
        var btnDefaultState = sharedPreferences!!.getBoolean("TOGGLE_DEFAULT", false)
        var btnTheme1State = sharedPreferences!!.getBoolean("TOGGLE_THEME1", false)
        var btnTheme2State = sharedPreferences!!.getBoolean("TOGGLE_THEME2", false)


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
        binding.toggleButtonTheme.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->

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
        binding.toggleButtonMode.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->

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

        //Gestione tocco bottone che serve a tornare al mainFragment e che inoltre invia un messaggio di feedback all'utente
        binding.btnApply.setOnClickListener {
            var mex=getString(R.string.changesAppliedMex)
            Toast.makeText(requireContext(), "${mex}", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }

        //Gestione tocco bottone che serve a tornare al mainFragment
        binding.backSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }

        return binding.root
    }
}
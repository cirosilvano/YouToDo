package com.icloud.ciro.silvano.youtodo

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ShareActionProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.color.DynamicColors
import com.icloud.ciro.silvano.youtodo.databinding.FragmentMainBinding
import com.icloud.ciro.silvano.youtodo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val my_themes = listOf("Theme_YouToDoWinter","Theme_YouToDoFall")
    private var sharedPreferences:SharedPreferences?=null
    var prefEditor:SharedPreferences.Editor?=null

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

        sharedPreferences=activity?.getSharedPreferences("themePref",MODE_PRIVATE)
        prefEditor=sharedPreferences?.edit() //permette di salvare il tema scelto nelle SharedPreferences

        //gestione del caso in cui si prema sulla freccia in alto a sinistra per tornare indietro
        binding.backSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }

        //gestione del caso in cui si prema sul primo tema
        binding.btnTheme1.setOnClickListener{
            //Salvataggio del tema nelle SharedPreferences
            activity?.setTheme(R.style.Theme_YouToDoWinter)
            prefEditor?.putInt("THEME",1)
            prefEditor?.commit()

            //Ritorno al MainFragment
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }

        //gestione del caso in cui si prema sul secondo tema
        binding.btnTheme2.setOnClickListener{
            //Salvataggio del tema nelle SharedPreferences
            activity?.setTheme(R.style.Theme_YouToDoFall)
            prefEditor?.putInt("THEME",2)
            prefEditor?.commit()

            //Ritorno al MainFragment
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }

        //gestione del caso in cui si prema sul bottone che resetta il tema a quello di default
        binding.btnReset.setOnClickListener{
            //Salvataggio del tema nelle SharedPreferences
            prefEditor?.putInt("THEME",0)
            prefEditor?.commit()
            activity?.recreate()

            //Ritorno al MainFragment
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }

        // Inflate the layout for this fragment
        return binding.root
    }


}
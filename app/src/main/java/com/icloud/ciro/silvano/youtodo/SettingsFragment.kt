package com.icloud.ciro.silvano.youtodo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.backSettingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }

        binding.btnTheme1.setOnClickListener{
            activity?.setTheme(R.style.Theme_YouToDoWinter)
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }

        binding.btnTheme2.setOnClickListener{
            activity?.setTheme(R.style.Theme_YouToDoFall)
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }


        binding.btnReset.setOnClickListener{
           // activity?.setTheme(R.style.Theme_YouToDo)
            activity?.recreate()
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)

        }

        // Inflate the layout for this fragment
        return binding.root
    }


}
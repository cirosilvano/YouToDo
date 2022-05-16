package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.findNavController

class AddFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view=inflater.inflate(R.layout.fragment_add, container, false)
       val btnBack = view.findViewById<ImageButton>(R.id.backAddButton)

       btnBack.setOnClickListener {
           val action=AddFragmentDirections.actionAddFragmentToMainFragment()
           view.findNavController().navigate(action)
       }

       return view
    }


}
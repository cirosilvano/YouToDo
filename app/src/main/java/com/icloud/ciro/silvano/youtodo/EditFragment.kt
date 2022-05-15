package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EditFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view=inflater.inflate(R.layout.fragment_edit, container, false)
       val btnEdit=view.findViewById<Button>(R.id.btn_edit)

       btnEdit.setOnClickListener {
           val action=EditFragmentDirections.actionEditFragmentToMainFragment()
           view.findNavController().navigate(action)
       }
       return view
    }


}
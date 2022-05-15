package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_main, container, false)
        val fabMain=view.findViewById<FloatingActionButton>(R.id.fabMain)

        /*Evento click del floatingActionButton*/
        fabMain.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToAddFragment()
                view.findNavController().navigate(action)
        }

        return view
    }


}
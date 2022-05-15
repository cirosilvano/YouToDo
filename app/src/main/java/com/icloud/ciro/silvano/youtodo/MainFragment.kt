package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    lateinit var todoRecyclerView: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_main, container, false)

       val addBtn=view.findViewById<ImageButton>(R.id.addButton)
        todoRecyclerView = view.findViewById(R.id.itemsList)

        

        /*Evento click dell'addButton*/
        addBtn.setOnClickListener {
            val action=MainFragmentDirections.actionMainFragmentToAddFragment()
                view.findNavController().navigate(action)
        }

        return view
    }


}
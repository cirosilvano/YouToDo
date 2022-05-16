package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtodo.ItemAdapter
import com.example.youtodo.databaseUtilities.ItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.icloud.ciro.silvano.youtodo.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemViewModel : ItemViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    lateinit var todoRecyclerView: RecyclerView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment

      _binding = FragmentMainBinding.inflate(inflater, container, false)

      val adapter = ItemAdapter()
      val recyclerView = binding.itemsList
      recyclerView.adapter = adapter
      recyclerView.layoutManager = LinearLayoutManager(requireContext())

      //ItemViewModel
      itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
      itemViewModel.showAllItems.observe(viewLifecycleOwner, Observer{ user ->
          adapter.setData(user)
      })

      binding.addButton.setOnClickListener {
          findNavController().navigate(R.id.action_mainFragment_to_addFragment)
      }

      return binding.root
    }


}
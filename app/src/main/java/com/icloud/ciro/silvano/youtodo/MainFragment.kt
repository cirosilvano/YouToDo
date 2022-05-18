package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.ItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemViewModel : ItemViewModel
    private  lateinit var ivFree:ImageView
    private lateinit var tvFree: TextView


    lateinit var todoRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment

         _binding = FragmentMainBinding.inflate(inflater, container, false)


        val adapter = ItemAdapter()
        val recyclerView = binding.itemsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val adapterCat=CategoryAdapter()
        val recyclerViewCat=binding.catList
        recyclerViewCat.adapter=adapterCat
        recyclerViewCat.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)


        var ivFree= binding.ivFree
        var tvFree=binding.tvFree
        //ItemViewModel
        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)


        itemViewModel.showAllItems.observe(viewLifecycleOwner, Observer{ user ->
            adapter.setData(user)
            if(adapter.itemCount>0){
               ivFree.isVisible=false
                tvFree.isVisible=false

            }
            else{
                ivFree.isVisible=true
                tvFree.isVisible=true
            }

        })

        itemViewModel.showAllCategories.observe(viewLifecycleOwner, Observer{ user ->
            adapterCat.setDataCat(user)

            if(adapterCat.itemCount==0){
                itemViewModel.addCategory(Category("Tutti"))
                itemViewModel.addCategory(Category("Lavoro"))
                itemViewModel.addCategory(Category("Personale"))
            }
        })

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFragment)
        }



        val bottomAppBar: BottomNavigationView = binding.bottomNavigationView

        bottomAppBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings_nav -> {
                    // Handle search icon press
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }

        return binding.root
    }
}
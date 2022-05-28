package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ItemViewModel
import com.icloud.ciro.silvano.youtodo.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val btnBack = binding.backCategoryButton
        val adapterCat=CategoryAdapter{}
        val recyclerView = binding.catList
        recyclerView.adapter = adapterCat
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_mainFragment)
        }

        itemViewModel.showAllCategories.observe(viewLifecycleOwner, Observer { cat ->
            adapterCat.setDataCat(cat)



        })
            return binding.root
    }
}
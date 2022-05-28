package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_mainFragment)
        }

        return binding.root
    }

}
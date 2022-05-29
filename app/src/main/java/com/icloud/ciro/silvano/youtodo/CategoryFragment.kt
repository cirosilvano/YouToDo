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
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.databinding.FragmentCategoryBinding


class CategoryFragment : Fragment(), CategoryListener {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var toDoViewModel: ToDoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val btnBack = binding.backCategoryButton
        val adapterCat=CategoryAdapter(this)
        val recyclerView = binding.catList
        recyclerView.adapter = adapterCat
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)

        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_mainFragment)
        }

        toDoViewModel.showAllCategories.observe(viewLifecycleOwner, Observer { cat ->
            adapterCat.setDataCat(cat)



        })
            return binding.root
    }

    override fun categoryEdit(category: Category) {
        /*val name = binding.editName.text.toString()
        val category = binding.editCategory.text.toString()
        val deadline = "${dateString}T${timeString}"



        if(inputCheck(name, category,deadline)){
            // Create Item Object
            val item = Item(args.currentItem.id, name, category, deadline, args.currentItem.isDone)

            // Add Data to Database
            itemViewModel.updateItem(item)

            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_LONG).show()

            // Navigate Back
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }*/
    }

    override fun categoryDelete(category: Category) {
        TODO("Not yet implemented")
    }
}
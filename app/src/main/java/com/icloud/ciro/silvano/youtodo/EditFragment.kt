package com.icloud.ciro.silvano.youtodo

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ItemViewModel
import com.icloud.ciro.silvano.youtodo.database.Item
import com.icloud.ciro.silvano.youtodo.databinding.FragmentEditBinding

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EditFragmentArgs>()

    private lateinit var itemViewModel: ItemViewModel
    private lateinit var  category : Category


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentEditBinding.inflate(inflater, container, false)


        val adapterCat=CategoryAdapter{model ->
            category=model
            binding.editCategory.setText(model.name)
        }
        val recyclerViewCat=binding.listCatEdit

       recyclerViewCat.adapter=adapterCat
        recyclerViewCat.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,false)

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        itemViewModel.showAllCategories.observe(viewLifecycleOwner, Observer{ user ->
            adapterCat.setDataCat(user)
        })

        // Inflate the layout for this fragment

        binding.editName.setText(args.currentItem.name)
        binding.editCategory.setText(args.currentItem.category)

        binding.btnEdit.setOnClickListener{
            updateItem()
        }


        // Eliminazione CardView con ImageButton
        val deleteEditButton : ImageButton = binding.deleteEditButton
        deleteEditButton.setOnClickListener{
            deleteCard()
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
            true
        }

        // Torna indietro
        val backEditButton: ImageButton = binding.backEditButton
        backEditButton.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }


        binding.btnDeleteCat.setOnClickListener {
            deleteCategory()
            binding.editCategory.setText("")
        }


        return binding.root
    }



    private fun updateItem() {
        val name = binding.editName.text.toString()
        val category = binding.editCategory.text.toString()
        // val deadline = binding.addDeadline.text.toString()

        /*Calendar management*/
        val day=binding.datePicker.dayOfMonth.toString()
        val month=binding.datePicker.month.toString()
        val year=binding.datePicker.year.toString()
        val deadline="${day}/${month}/${year}"

        if(inputCheck(name, category)){
            // Create Item Object
            val item = Item(args.currentItem.id, name, category, deadline)

            // Add Data to Database
            itemViewModel.updateItem(item)

            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_LONG).show()

            // Navigate Back
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }


    }

    private fun inputCheck(name : String, category: String) : Boolean {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(category))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_delete,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_delete){
            deleteCard()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteCard() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _,_ ->
            itemViewModel.deleteItem(args.currentItem)
            Toast.makeText(requireContext(), "Successfully removed ${args.currentItem.name}!", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        builder.setTitle("Are you sure you want to delete ${args.currentItem.name} ?")
        builder.setMessage("Are you sure you want to delete ${args.currentItem.name} ?")

        builder.create().show()
    }


    private fun deleteCategory() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _,_ ->
            itemViewModel.deleteCategory(category)
            Toast.makeText(requireContext(), "Successfully removed \"${category.name} \" category !", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        builder.setTitle("Are you sure you want to delete \"${category.name} \" category ?")
        builder.setMessage("Are you sure you want to delete\"${category.name} \" category ?")

        builder.create().show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.youtodo.databaseUtilities.Item

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

    private fun insertItemToDatabase() {
        val name = binding.addName.text.toString()
        val category = binding.addCategory.text.toString()
        val deadline = binding.addDeadline.text.toString()

        if(inputCheck(name, category, deadline)){
            // Create Item Object
            val item = Item(0, name, category, deadline)

            // Add Data to Database
            itemViewModel.addItem(item)

            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()

            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_mainFragment)
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(name : String, category: String, deadline : String) : Boolean {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(deadline))
    }


}
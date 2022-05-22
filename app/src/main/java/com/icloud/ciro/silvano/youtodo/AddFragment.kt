package com.icloud.ciro.silvano.youtodo

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ItemViewModel
import com.icloud.ciro.silvano.youtodo.database.Item
import com.icloud.ciro.silvano.youtodo.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var chipGroupAdd:ChipGroup



   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       _binding = FragmentAddBinding.inflate(inflater, container, false)
       val btnBack = binding.backAddButton
       chipGroupAdd=binding.chipGroupAdd
       itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

       btnBack.setOnClickListener {
           findNavController().navigate(R.id.action_addFragment_to_mainFragment)
       }

       val adapterCat=CategoryAdapter{model ->
           //binding.editAddCategory.setText(model.name)
       }

       itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

       itemViewModel.showAllCategories.observe(viewLifecycleOwner, Observer{ cat ->
           adapterCat.setDataCat(cat)

           if(adapterCat.itemCount==0){
               itemViewModel.addCategory(Category("Tutti"))
               itemViewModel.addCategory(Category("Lavoro"))
               itemViewModel.addCategory(Category("Personale"))
               chipGroupAdd.addChip(requireActivity(),"Tutti")
               chipGroupAdd.addChip(requireActivity(),"Lavoro")
               chipGroupAdd.addChip(requireActivity(),"Personale")
           }

           for(i in cat){
               var found:Boolean=false
               for(j in chipGroupAdd.children){
                   var currChip= j as Chip
                   if(i.name==currChip.text){
                       found=true
                   }
               }
               if(!found)
                   chipGroupAdd.addChip(requireActivity(),i.name)
           }
       })


    /*Gestione click della chip: se una chip viene cliccata, si prende il suo
    *nome e lo si inserisce nella editText sottostante*/

       chipGroupAdd.setOnCheckedChangeListener { _, checkedId ->
           (chipGroupAdd.findViewById<Chip>(checkedId))?.let {
               it.setOnClickListener {
                   var myChip:Chip=it as Chip
                   binding.editAddCategory.setText(myChip.text.toString())
               }
           }
       }

       /* Gestione inserimento nuova categoria*/

       binding.editAddCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
           if(keyCode== KeyEvent.KEYCODE_ENTER && event.action== KeyEvent.ACTION_UP) {
               if(existingCat(binding.editAddCategory.text.toString())) {
                   Toast.makeText(requireContext(), "Existing category !", Toast.LENGTH_LONG).show()
               }
               else{
                   chipGroupAdd.addChip(requireActivity(), binding.editAddCategory.text.toString())
                   itemViewModel.addCategory(Category(binding.editAddCategory.text.toString()))
               }
               true

           }
           false
       })



       binding.btnAdd.setOnClickListener {
           insertItemToDatabase()
       }



       return binding.root
    }

    private fun insertItemToDatabase(): Boolean {
        val name = binding.addName.text.toString()
        val category = binding.editAddCategory.text.toString()
       // val deadline = binding.addDeadline.text.toString()

        /*Gestione del calendario*/
        val day=binding.datePicker.dayOfMonth.toString()
        val month=binding.datePicker.month.toString()
        val year=binding.datePicker.year.toString()
        val deadline="${day}/${month}/${year}"
        if(inputCheck(name, category, deadline)){
            // Create Item Object
            val item = Item(0, name, category, deadline)
            val category= Category(category)
            // Add Data to Database

            itemViewModel.addItem(item)
            itemViewModel.addCategory(category)



            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()

            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_mainFragment)
            return true
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
            return false
        }


    }

    private fun inputCheck(name : String, category: String, deadline : String) : Boolean {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(deadline))
    }

    // create chip programmatically and add it to chip group
    private fun ChipGroup.addChip(context: Context?, label: String){
        Chip(context).apply {
            id = View.generateViewId()
            text = label
            isClickable = true
            isCheckable = true
            isCheckedIconVisible = true
            isCloseIconVisible = false
            isFocusable = true
            addView(this)
            this.setOnCloseIconClickListener{

                //Eliminazione dell'elemento dalla tabella
                itemViewModel.deleteCategory(Category(this.text.toString()))
                //Rimozione della chip
                removeView(this)
            }
        }
    }


    private fun existingCat(name:String):Boolean {
        var found:Boolean=false
        for(j in chipGroupAdd.children){
            var currChip= j as Chip
            if(currChip.text.toString()==name || currChip.text.toString()==name+" "){
                found=true
            }
        }
        return found
    }


}
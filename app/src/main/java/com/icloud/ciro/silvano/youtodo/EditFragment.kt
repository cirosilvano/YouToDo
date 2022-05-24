package com.icloud.ciro.silvano.youtodo

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
    private lateinit var chipGroupEdit:ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentEditBinding.inflate(inflater, container, false)
         chipGroupEdit=binding.chipGroupEdit


        val adapterCat=CategoryAdapter{model ->
            /*category=model
            binding.editCategory.setText(model.name)*/
        }


        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

       itemViewModel.showAllCategories.observe(viewLifecycleOwner, Observer{ cat ->
            adapterCat.setDataCat(cat)
           if(adapterCat.itemCount==0){
               itemViewModel.addCategory(Category("Tutti"))
               itemViewModel.addCategory(Category("Lavoro"))
               itemViewModel.addCategory(Category("Personale"))
               chipGroupEdit.addChip(requireActivity(),"Tutti")
               chipGroupEdit.addChip(requireActivity(),"Lavoro")
               chipGroupEdit.addChip(requireActivity(),"Personale")
           }

           for(i in cat){
               var found:Boolean=false
               for(j in chipGroupEdit.children){
                   var currChip= j as Chip
                   if(i.name==currChip.text){
                       found=true
                   }
               }
               if(!found)
                   chipGroupEdit.addChip(requireActivity(),i.name)
           }
        })

        binding.editName.setText(args.currentItem.name)
        binding.editCategory.setText(args.currentItem.category)

        chipGroupEdit.setOnCheckedChangeListener { _, checkedId ->
            (chipGroupEdit.findViewById<Chip>(checkedId))?.let {
                it.setOnClickListener {
                    var myChip:Chip=it as Chip
                    binding.editCategory.setText(myChip.text.toString())
                }
            }
        }

        /* Gestione inserimento nuova categoria*/

        binding.editCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
            if(keyCode== KeyEvent.KEYCODE_ENTER && event.action== KeyEvent.ACTION_UP){
                if(existingCat(binding.editCategory.text.toString())) {
                    Toast.makeText(requireContext(), "Existing category !", Toast.LENGTH_LONG).show()
                }
                else{

                    chipGroupEdit.clearCheck()
                    chipGroupEdit.addChip(requireActivity(), binding.editCategory.text.toString())
                    itemViewModel.addCategory(Category(binding.editCategory.text.toString()))
                }
                true
            }
            false
        })

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
            val item = Item(args.currentItem.id, name, category, deadline, args.currentItem.isDone)

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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        }
    }

    private fun existingCat(name:String):Boolean {
        var found:Boolean=false
        for(j in chipGroupEdit.children){
            var currChip= j as Chip
            if(currChip.text.toString().toLowerCase()==name.toLowerCase() || currChip.text.toString().toLowerCase()==(name+" ").toLowerCase()){
                found=true
            }
        }
        return found
    }


}
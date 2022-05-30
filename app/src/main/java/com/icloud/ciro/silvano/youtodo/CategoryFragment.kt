package com.icloud.ciro.silvano.youtodo

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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

        //Gestione FAB per aggiungere una nuova categoria
        binding.FabAddCat.setOnClickListener{
            binding.txtTitleActionCat.setText(R.string.addCatTitle)
            binding.catList.isVisible=false
            binding.txtTitleActionCat.isVisible=true
            binding.txtFieldCategory.isVisible=true
            binding.btnBackMainCat.isVisible=true
            binding.backCategoryButton.isVisible=false
            binding.FabAddCat.isVisible=false

            binding.btnBackMainCat.setOnClickListener{
                binding.txtTitleActionCat.isVisible=false
                binding.txtFieldCategory.isVisible=false
                binding.catList.isVisible=true
                binding.txtFieldCategory.setText("")
                binding.btnBackMainCat.isVisible=false
                binding.backCategoryButton.isVisible=true
                binding.FabAddCat.isVisible=true
            }
            binding.txtFieldCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
                if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){

                    var newVal=binding.txtFieldCategory.text.toString().trim().lowercase()
                    if(!newVal.isEmpty()){
                        var result=toDoViewModel.addCatLong(Category(newVal))
                        if(result>0){
                            Toast.makeText(requireContext(), "Successfully Added!", Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(requireContext(), "Existing category!", Toast.LENGTH_LONG).show()
                        }
                        binding.txtTitleActionCat.isVisible=false
                        binding.txtFieldCategory.isVisible=false
                        binding.btnBackMainCat.isVisible=false
                        binding.catList.isVisible=true
                        binding.backCategoryButton.isVisible=true
                        binding.FabAddCat.isVisible=true
                        binding.txtFieldCategory.setText("")
                    }
                    else{
                        Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
                    }
                    true
                }

                false
            })
        }

        return binding.root
    }

    //metodo per la gestione della modifica della categoria
    override fun categoryEdit(category: Category) {
        binding.txtTitleActionCat.setText(R.string.editCatTitle)
        binding.txtFieldCategory.setText(category.name.toString())
        var oldName=category.name.toString()
        binding.btnBackMainCat.isVisible=true
        binding.catList.isVisible=false
        binding.txtTitleActionCat.isVisible=true
        binding.txtFieldCategory.isVisible=true
        binding.backCategoryButton.isVisible=false
        binding.FabAddCat.isVisible=false
        binding.btnBackMainCat.setOnClickListener{
            binding.txtTitleActionCat.isVisible=false
            binding.txtFieldCategory.isVisible=false
            binding.catList.isVisible=true
            binding.txtFieldCategory.setText("")
            binding.btnBackMainCat.isVisible=false
            binding.backCategoryButton.isVisible=true
            binding.FabAddCat.isVisible=true
        }

        binding.txtFieldCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
            if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){

                var newName=binding.txtFieldCategory.text.toString().trim().lowercase()
                if(!newName.isEmpty()){
                    toDoViewModel.updateCategory(oldName,newName)
                    Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_LONG).show()
                    binding.txtTitleActionCat.isVisible=false
                    binding.txtFieldCategory.isVisible=false
                    binding.btnBackMainCat.isVisible=false
                    binding.catList.isVisible=true
                    binding.backCategoryButton.isVisible=true
                    binding.FabAddCat.isVisible=true
                    binding.txtFieldCategory.setText("")
                }
                else{
                    Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
                }
                true
            }

            false
        })
    }

    ////metodo per la gestione dell'eliminazione della categoria
    override fun categoryDelete(category: Category) {
        var success : Int = 0
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _,_ ->
            try {
                success = toDoViewModel.deleteCategory(category)
            } catch(e : android.database.sqlite.SQLiteConstraintException) {
                Toast.makeText(requireContext(), "Impossibile eliminarla !TOdo sotto tale categoria", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(requireContext(), "category successfully removed !", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        builder.setTitle("Are you sure you want to delete ${category.name} ?")
        builder.setMessage("Are you sure you want to delete ${category.name} ?")

        builder.create().show()
    }
}
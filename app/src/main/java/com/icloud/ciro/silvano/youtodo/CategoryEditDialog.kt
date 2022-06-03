package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.databinding.FragmentCategoryEditDialogBinding


class CategoryEditDialog(category: Category) : DialogFragment() {

    private var _binding: FragmentCategoryEditDialogBinding? = null
    private val binding get()=_binding!!
    private val cat : Category = category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentCategoryEditDialogBinding.inflate(inflater, container, false)

        binding.textView.text = "Edit \""+cat.name+"\""
        binding.editCategory.setText(cat.name)

        val toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)

        binding.confirm.isEnabled = false

        binding.editCategory.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.confirm.isEnabled = s!!.isNotEmpty()
                binding.confirm.isEnabled = s.toString().trim().lowercase() != cat.name.trim().lowercase()
            }
        })

        binding.confirm.setOnClickListener {
            var newVal=binding.editCategory.text.toString().trim().lowercase()

            if(newVal.isNotEmpty() && newVal != cat.name.trim().lowercase())
                toDoViewModel.updateCategory(cat.name, newVal)

            dismiss()
        }

        binding.dismiss.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}
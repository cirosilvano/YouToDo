package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.databinding.FragmentCategoryEditDialogBinding


class CategoryEditDialog(category: Category) : DialogFragment() {

    private var _binding: FragmentCategoryEditDialogBinding? = null
    private val binding get()=_binding!!
    private val cat : Category = category

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentCategoryEditDialogBinding.inflate(inflater, container, false)

        binding.textView.text = getString(R.string.editDialogTitle, cat.name)
        binding.editCategory.setText(cat.name)

        val toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)

        binding.confirm.isEnabled = false

        binding.editCategory.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.confirm.isEnabled =
                    !(s!!.isEmpty() && s.toString().trim().lowercase() != cat.name.trim().lowercase())

                if(s.length>15){
                    binding.editCategory.error = getString(R.string.maxNumChar)
                    binding.textInputCategoryLayout.endIconMode = END_ICON_NONE
                }
                else{
                    binding.editCategory.error = null
                    binding.textInputCategoryLayout.endIconMode = END_ICON_CLEAR_TEXT
                }
            }
        })

        binding.confirm.setOnClickListener {
            val newVal=binding.editCategory.text.toString().trim().lowercase()

            if(newVal.length>15){
                Toast.makeText(requireContext(), getString(R.string.maxNumChar), Toast.LENGTH_SHORT).show()

            }
            else {
                if (newVal.isNotEmpty() && newVal != cat.name.trim().lowercase())
                    toDoViewModel.updateCategory(cat.name, newVal)

            dismiss()
            }
        }

        binding.dismiss.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}
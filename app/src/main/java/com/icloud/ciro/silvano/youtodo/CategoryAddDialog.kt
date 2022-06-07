package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.databinding.FragmentCategoryAddDialogBinding

class CategoryAddDialog : DialogFragment() {

    private var _binding: FragmentCategoryAddDialogBinding? = null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentCategoryAddDialogBinding.inflate(inflater, container, false)

        val toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)

        binding.confirm.isEnabled = false

        binding.addCategory.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.confirm.isEnabled = s!!.isNotEmpty()
                if(s.length>20){
                    binding.addCategory.error = getString(R.string.maxNumChar)
                    binding.textInputCategoryLayout.endIconMode = TextInputLayout.END_ICON_NONE

                }
                else{
                    binding.addCategory.error = null
                    binding.textInputCategoryLayout.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

                }
            }
        })

        binding.confirm.setOnClickListener {
            val newVal=binding.addCategory.text.toString().trim().lowercase()

            if(newVal.length>20){
                Toast.makeText(requireContext(), getString(R.string.maxNumChar), Toast.LENGTH_SHORT).show()

            }
            else{
                 if(newVal.isNotEmpty()) {
                     toDoViewModel.addCatLong(Category(newVal))
                 }

            dismiss()
            }
        }

        binding.dismiss.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

}
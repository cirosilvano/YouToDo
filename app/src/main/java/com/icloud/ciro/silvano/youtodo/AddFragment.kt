package com.icloud.ciro.silvano.youtodo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDate
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ItemViewModel
import com.icloud.ciro.silvano.youtodo.database.Item
import com.icloud.ciro.silvano.youtodo.databinding.FragmentAddBinding

class AddFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CategoryListener {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var chipGroupAdd:ChipGroup

    // VARIABILI DI SUPPORTO DATE-TIME
    var year = 0
    var month = 0
    var day = 0
    var hour = 0
    var minute = 0

    var savedYear = 0
    var savedMonth = 0
    var savedDay = 0
    var savedHour = 0
    var savedMinute = 0

    var dateString = ""
    var timeString = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       _binding = FragmentAddBinding.inflate(inflater, container, false)
       val btnBack = binding.backAddButton
       chipGroupAdd=binding.chipGroupAdd
       itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

       btnBack.setOnClickListener {
           findNavController().navigate(R.id.action_addFragment_to_mainFragment)
       }

       // GESTIONE DATE-TIME PICKING
       binding.editDate.setOnFocusChangeListener{view,b->
           if(b) {
               getDateTimeCalendar()
               DatePickerDialog(requireContext(), this, year, month, day).show()
           }
       }

       binding.editTime.setOnFocusChangeListener{view,b->
           if(b) {
               getDateTimeCalendar()
               TimePickerDialog(requireContext(), this, hour, minute, true).show()
           }
       }

       // fine time picking -------


       val adapterCat=CategoryAdapter(this)

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
           if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){
               Log.d("","CATEGORIA INSERITA TEXTFIELD:${binding.editAddCategory.text.toString()}")

               var textVal=binding.editAddCategory.text.toString().trim()
               if(existingCat(textVal)) {
                   Toast.makeText(requireContext(), "Existing category !", Toast.LENGTH_LONG).show()

               }
              else if(textVal.isEmpty()){
                   Toast.makeText(requireContext(), "Choose a category !", Toast.LENGTH_LONG).show()
                   binding.editAddCategory.setText("")
                     }
                    else{
                        chipGroupAdd.addChip(requireActivity(), binding.editAddCategory.text.toString())
                        itemViewModel.addCategory(Category(binding.editAddCategory.text.toString()))
                        chipGroupAdd.clearCheck()
                    }
               binding.editAddCategory.setText("")
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
        val deadline = "${dateString}T${timeString}"

        Log.d("", "inserimento item con deadline $deadline")

        if(inputCheck(name, category, deadline)){
            // Create Item Object
            val item = Item(0, name, category, deadline, false)
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
        }
    }


    private fun existingCat(name:String):Boolean {
        var found=false
        Log.d("","CATEGORIA INSERITA:${name.toString()}")
        for(j in chipGroupAdd.children){
            var currChip= j as Chip
            Log.d("","CATEGORIA CHIP:${currChip.text.toString()}")
            if(name.toLowerCase().contains(currChip.text.toString().toLowerCase())){
                Log.d("","ENTRO")

                found=true
                break
            }
        }
        Log.d("","TROVATA? ${found}")
        return found
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear = p1
        savedMonth = p2+1
        savedDay = p3
        getDateTimeCalendar()
        binding.editDate.setText(generateDate(savedYear, savedMonth, savedDay, true))
        dateString = generateDate(savedYear, savedMonth, savedDay, false)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour = p1
        savedMinute = p2
        getDateTimeCalendar()
        val gen = generateTime(savedHour, savedMinute)
        binding.editTime.setText(gen)
        timeString = "${gen}:00"
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun categoryEdit(category: Category) {
        TODO("Not yet implemented")
    }

    override fun categoryDelete(category: Category) {
        TODO("Not yet implemented")
    }


}
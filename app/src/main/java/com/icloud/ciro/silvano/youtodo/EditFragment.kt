package com.icloud.ciro.silvano.youtodo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TimePicker
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

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        chipGroupEdit=binding.chipGroupEdit





        // fine time picking -------

        val adapterCat=CategoryAdapter{model ->
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
        }


    }

    private fun inputCheck(name : String, category: String, deadline : String) : Boolean {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(deadline))
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




    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun generateDate(year:Int, month: Int, day: Int, backwards: Boolean): String {
        var monthStr = (month+1).toString()
        var dayStr = day.toString()
        if(monthStr.length == 1) monthStr = "0${monthStr}"
        if(dayStr.length == 1) monthStr = "0${dayStr}"
        Log.d("", "genero data $dayStr/$monthStr/$year")
        if(backwards) return "$dayStr-$monthStr-$year"
        return "$year-$monthStr-$dayStr"
    }

    private fun generateTime(hour:Int, minute:Int): String{
        var hourString = hour.toString()
        var minuteString = minute.toString()
        if(hourString.length == 1) hourString = "0${hourString}"
        if(minuteString.length == 1) minuteString = "0${minuteString}"
        return "$hourString:$minuteString"
    }


}
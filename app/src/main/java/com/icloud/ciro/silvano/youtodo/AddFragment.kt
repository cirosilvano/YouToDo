package com.icloud.ciro.silvano.youtodo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDate
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.database.Card
import com.icloud.ciro.silvano.youtodo.databinding.FragmentAddBinding

class AddFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CategoryListener {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var chipGroupAdd:ChipGroup

    // VARIABILI DI SUPPORTO DATE-TIME
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0

    private var savedYear = 0
    private var savedMonth = 0
    private var savedDay = 0
    private var savedHour = 0
    private var savedMinute = 0

    private var dateString = ""
    private var timeString = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_left)
    }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
       //Inflate del layout per il fragment
       _binding = FragmentAddBinding.inflate(inflater, container, false)

       //Identificazione del bottone a forma di freccia in alto a sinistra che serve a tornare al MainFragment
       val btnBack = binding.backAddButton

       //Inizializzaizone del chipGroup che ospiterà le chip conteneti le categorie
       chipGroupAdd=binding.chipGroupAdd

       //ViewModel per comunicare con la repository
       toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)

       //Gestione dell'evento click su bottone che serve a tornare al MainFragme
       btnBack.setOnClickListener {
           findNavController().navigate(R.id.action_addFragment_to_mainFragment)
       }

       // GESTIONE DATE-TIME PICKING
       binding.addDate.setOnFocusChangeListener{ _, b->
           if(b) {
               getDateTimeCalendar()
               DatePickerDialog(requireContext(), this, year, month, day).show()
               binding.addDate.clearFocus()
           }
       }


       binding.addTime.setOnFocusChangeListener{_, b->
           if(b) {
               getDateTimeCalendar()
               TimePickerDialog(requireContext(), this, hour, minute, true).show()
               binding.addTime.clearFocus()


           }
       }

       // fine time picking -------

       //Creazione dell'adapter per le categorie
       val adapterCat=CategoryAdapter(this)

       //Creazione dell'observer per mostrare tutte le categorie presenti nel database all'interno della recyclerView
       //Essendo la lista delle categorie una LiveData Observer si occuperà in tempo reale di aggiornare il contenuto della recyclerView
       //Il codice all'interno dell'observer sarà eseguito solo se:
       //-gli elementi all'interno del database subiscono modifiche di qualsiasi tipo (aggiunta, rimozione, update)
       //-l'observer passa dallo stato inattivo ad attivo
       toDoViewModel.showAllCategories.observe(viewLifecycleOwner) { cat ->
           adapterCat.setDataCat(cat)
            /*Creazione delle chip di default propposte all'utente.
            * Si è deciso di riporle ogni qualvolta l'utente elimina TUTTE le categorie esistenti nella tabella
            * per far in modo che esista sempre come minimo una categoria che esso possa scegliere
            */
           if(adapterCat.itemCount==0){
               toDoViewModel.addCategory(Category(getString(R.string.all)))
               toDoViewModel.addCategory(Category(getString(R.string.work)))
               toDoViewModel.addCategory(Category(getString(R.string.personal)))
               chipGroupAdd.addChip(requireActivity(),getString(R.string.all))
               chipGroupAdd.addChip(requireActivity(),getString(R.string.work))
               chipGroupAdd.addChip(requireActivity(),getString(R.string.personal))
           }

           for(i in cat){
               var found = false
               for(j in chipGroupAdd.children){
                   val currChip= j as Chip
                   if(i.name==currChip.text){
                       found=true
                   }
               }
               if(!found)
                   chipGroupAdd.addChip(requireActivity(),i.name)
           }
       }

       /*
       * Gestione click della chip: se una chip viene cliccata, si prende il suo
       * nome e lo si inserisce nella editText sottostante
       */
       chipGroupAdd.setOnCheckedChangeListener { _, checkedId ->
           (chipGroupAdd.findViewById<Chip>(checkedId))?.let { it ->
               it.setOnClickListener { chip ->
                   val myChip: Chip = chip as Chip
                   binding.editAddCategory.setText(myChip.text.toString())
               }
           }
       }

       /* Gestione inserimento nella tabella category della nuova categoria creata dall'utente*/
       binding.editAddCategory.setOnKeyListener { _, keyCode, event ->
           if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){

               val textVal=binding.editAddCategory.text.toString().trim()
               if(existingCat(textVal)) {
                   Toast.makeText(requireContext(), getString(R.string.ExCat), Toast.LENGTH_LONG).show()

               }
               else if(textVal.isEmpty()) {
                   Toast.makeText(requireContext(), getString(R.string.ChooseCat), Toast.LENGTH_LONG).show()
                   binding.editAddCategory.setText("")
                     }
                    else{
                        /*controllo che la lunghezza della categoria sia adeguata*/
                        if(textVal.length>20){
                            Toast.makeText(requireContext(), getString(R.string.maxNumChar), Toast.LENGTH_LONG).show()
                         }
                        else{
                        chipGroupAdd.addChip(requireActivity(), binding.editAddCategory.text.toString())
                        toDoViewModel.addCategory(Category(binding.editAddCategory.text.toString()))
                        chipGroupAdd.clearCheck()
                        }
                    }
               binding.editAddCategory.setText("")
           }

           false
       }

       /*controllo che la lunghezza della categoria: se l'utente sfora il numero massimo di caratteri, viene segnalato l'errore*/
       binding.editAddCategory.doOnTextChanged { text, _, _, _ ->
           if(text!!.length>20){
               binding.editAddCategoryLayout.error = getString(R.string.maxNumChar)
           }
           else{
               binding.editAddCategoryLayout.error = null
           }
       }
       /*Gestione dell' evento click sul bottone ADD che si trova in fondo alla schermata*/
       binding.btnAdd.setOnClickListener {
           insertItemToDatabase()
       }
       return binding.root
    }



    /**
     * Funzione che serve a gestire il corretto inserimento del nuovo "impegno" dell'utente nel database
     * @return true se l'inserimento è andato a buon fine, false altrimenti
     */
    private fun insertItemToDatabase(): Boolean {
        val name = binding.addName.text.toString()
        val category = binding.editAddCategory.text.toString()
        val deadline = "${dateString}T${timeString}"


        if(inputCheck(name, category, deadline)){
            // Create Item Object
            val card = Card(0, name, category, deadline, false)
            val cat = Category(category)

            toDoViewModel.addCategory(cat)
            toDoViewModel.addCard(card)

            Toast.makeText(requireContext(), getString(R.string.cardAddSucc), Toast.LENGTH_LONG).show()

            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_mainFragment)
            return true
        }
        else{
            Toast.makeText(requireContext(), getString(R.string.fillAllFields), Toast.LENGTH_LONG).show()
            return false
        }
    }

    /**
     * Funzione che controlla che l'utente non abbia lasciato campi di inserimento di testo vuoti
     * @param todo, testo che descrive l'impegno dell'utente
     * @param category, categoria dell'impegno
     * @param deadline, data e ora dell'impegno
     * @return true se l'input è corretto, false altrimenti
     */
     private fun inputCheck(todo : String, category: String, deadline : String) : Boolean {
        return !(TextUtils.isEmpty(todo) || TextUtils.isEmpty(category) || deadline.length != 19)
     }

    /**
     * Funzione che crea e aggiunge una chip al groupChip programmaticamente
     * @param context, contesto
     * @param label, testo da inserire nella chip
     */
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

    /**
     * Funzione che controlla se la categoria inserita dall'utente è tra quelle già presenti nel database
     * @param name, nome della categoria
     * @return true se la categoria esiste, false altrimenti
     */
    private fun existingCat(name:String):Boolean {
        var found=false

        for(j in chipGroupAdd.children){
            val currChip= j as Chip
            if(name.lowercase() == currChip.text.toString().lowercase()) {
                found=true
                break
            }
        }
        return found
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear = p1
        savedMonth = p2+1
        savedDay = p3
        getDateTimeCalendar()
        binding.addDate.setText(generateDate(savedYear, savedMonth, savedDay, true))
        dateString = generateDate(savedYear, savedMonth, savedDay, false)
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour = p1
        savedMinute = p2
        getDateTimeCalendar()
        val gen = generateTime(savedHour, savedMinute)
        binding.addTime.setText(gen)
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

    override fun categoryEdit(category: Category) {}
    override fun categoryDelete(category: Category) {}
}
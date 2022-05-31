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
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDate
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.database.Card
import com.icloud.ciro.silvano.youtodo.databinding.FragmentEditBinding
import java.time.LocalDateTime

class EditFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CategoryListener {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EditFragmentArgs>()
    private lateinit var toDoViewModel: ToDoViewModel
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
        //Inflate del layout per il fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        chipGroupEdit=binding.chipGroupEdit

        //Creazione dell'adapter per le categorie
        val adapterCat=CategoryAdapter(this)

        //ViewModel per comunicare con la repository
        toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)


        //Creazione dell'observer per mostrare tutte le categorie presenti nel database all'interno della recyclerView
        //Essendo la lista delle categorie una LiveData Observer si occuperà in tempo reale di aggiornare il contenuto della recyclerView
        //Il codice all'interno dell'observer sarà eseguito solo se:
        //-gli elementi all'interno del database subiscono modifiche di qualsiasi tipo (aggiunta, rimozione, update)
        //-l'observer passa dallo stato inattivo ad attivo
        toDoViewModel.showAllCategories.observe(viewLifecycleOwner, Observer{ cat ->
            adapterCat.setDataCat(cat)
           if(adapterCat.itemCount==0){
               /*Creazione delle chip di default propposte all'utente.
                * Si è deciso di riporle ogni qualvolta l'utente elimina TUTTE le categorie esistenti nella tabella
                * per far in modo che esista sempre come minimo una categoria che esso possa scegliere
                */
               toDoViewModel.addCategory(Category("Tutti"))
               toDoViewModel.addCategory(Category("Lavoro"))
               toDoViewModel.addCategory(Category("Personale"))
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

        //Riempimento dei campi di inserimento con i valori della card
        binding.editName.setText(args.currentCard.name)
        binding.editCategory.setText(args.currentCard.category)
        val ldt = LocalDateTime.parse(args.currentCard.deadline)
        binding.editDate.setText(generateDate(ldt.year, ldt.monthValue, ldt.dayOfMonth, true))
        binding.editTime.setText(generateTime(ldt.hour, ldt.minute))
        dateString = generateDate(ldt.year, ldt.monthValue, ldt.dayOfMonth, false)
        timeString = "${generateTime(ldt.hour, ldt.minute)}:00"

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

        /*
        * Gestione click della chip: se una chip viene cliccata, si prende il suo
        * nome e lo si inserisce nella editText sottostante
        */
        chipGroupEdit.setOnCheckedChangeListener { _, checkedId ->
            (chipGroupEdit.findViewById<Chip>(checkedId))?.let {
                it.setOnClickListener {
                    var myChip:Chip=it as Chip
                    binding.editCategory.setText(myChip.text.toString())
                }
            }
        }

        /* Gestione inserimento nella tabella category della nuova categoria creata dall'utente*/
        binding.editCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
            if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){
                Log.d("","CATEGORIA INSERITA TEXTFIELD:${binding.editCategory.text.toString()}")

                var textVal=binding.editCategory.text.toString().trim()
                if(existingCat(textVal)) {
                    Toast.makeText(requireContext(), "Existing category !", Toast.LENGTH_LONG).show()

                }
                else if(textVal.isEmpty()){
                    Toast.makeText(requireContext(), "Choose a category !", Toast.LENGTH_LONG).show()
                    binding.editCategory.setText("")
                }
                else{
                    chipGroupEdit.addChip(requireActivity(), binding.editCategory.text.toString())
                    toDoViewModel.addCategory(Category(binding.editCategory.text.toString()))
                    chipGroupEdit.clearCheck()
                }
                binding.editCategory.setText("")
                true
            }

            false
        })

        /*Gestione dell' evento click sul bottone EDIT che si trova in fondo alla schermata*/
        binding.btnEdit.setOnClickListener{
            updateItem()
        }


        // Eliminazione CardView con ImageButton a forma di bidona in alto a destra della schermata
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


    /**
     * Funzione che serve a gestire la modifica della card nel database
     */
    private fun updateItem() {
        val name = binding.editName.text.toString()
        val category = binding.editCategory.text.toString()
        val deadline = "${dateString}T${timeString}"

        if(inputCheck(name, category,deadline)){
            // Crezione della card
            val card = Card(args.currentCard.id, name, category, deadline, args.currentCard.isDone)

            // Aggiornamento della card nel database
            toDoViewModel.updateCard(card)

            Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_LONG).show()

            // Torna indietro al MainFragment
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }
        else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }


    }
    /**
     * Funzione che controlla che l'utente non abbia lasciato campi di inserimento di testo vuoti
     * @param todo, testo che descrive l'impegno dell'utente
     * @param category, categoria dell'impegno
     * @param deadline, data e ora dell'impegno
     * @return true se l'input è corretto, false altrimenti
     */
    private fun inputCheck(name : String, category: String, deadline : String) : Boolean {
        return !(TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(deadline))
    }

    /**
     * Funzione che serve a creare il menu (i tre punti) nella topToolBar
     * @param menu, il menu sul quale fare l'inflate
     * @param inflater, per impostare il layout del menù*/
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_delete,menu)
    }


    /**
     * Funzione che serve a gestire l'evento di click su un item del menu della topToolBar
     * @param item, item selezionato del menù
     * @return true se l'operazione è anadata a buon fine*/
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

    /**
     * Funzione che serve a gestire l'evento di eliminazione della card
    */
    private fun deleteCard() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _,_ ->
            toDoViewModel.deleteCard(args.currentCard)
            Toast.makeText(requireContext(), "Successfully removed ${args.currentCard.name}!", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        builder.setTitle("Are you sure you want to delete ${args.currentCard.name} ?")
        builder.setMessage("Are you sure you want to delete ${args.currentCard.name} ?")

        builder.create().show()
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
        Log.d("","CATEGORIA INSERITA:${name.toString()}")
        for(j in chipGroupEdit.children){
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




    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear = p1
        savedMonth = p2+1
        savedDay = p3
        getDateTimeCalendar()
        binding.editDate.setText(generateDate(savedYear, savedMonth, savedDay, true))
        dateString = generateDate(savedYear, savedMonth, savedDay, false)
        Log.d("DATE SET", "set dateString to $dateString")
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour = p1
        savedMinute = p2
        getDateTimeCalendar()
        val gen = generateTime(savedHour, savedMinute)
        binding.editTime.setText(gen)
        timeString = "${gen}:00"
        Log.d("TIME SET", "set timeString to $timeString")
    }

    override fun categoryEdit(category: Category) {
    }

    override fun categoryDelete(category: Category) {
    }


}
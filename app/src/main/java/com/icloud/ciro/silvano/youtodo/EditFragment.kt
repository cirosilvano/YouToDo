package com.icloud.ciro.silvano.youtodo

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
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDate
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.icloud.ciro.silvano.youtodo.database.Card
import com.icloud.ciro.silvano.youtodo.databinding.FragmentEditBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EditFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, CategoryListener {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EditFragmentArgs>()
    private lateinit var toDoViewModel: ToDoViewModel
    private lateinit var chipGroupEdit:ChipGroup

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
        toDoViewModel.showAllCategories.observe(viewLifecycleOwner) { cat ->
            adapterCat.setDataCat(cat)
           if(adapterCat.itemCount==0){
               /*Creazione delle chip di default propposte all'utente.
                * Si è deciso di riporle ogni qualvolta l'utente elimina TUTTE le categorie esistenti nella tabella
                * per far in modo che esista sempre come minimo una categoria che esso possa scegliere
                */
               toDoViewModel.addCategory(Category(getString(R.string.work)))
               toDoViewModel.addCategory(Category(getString(R.string.personal)))

           }

           for(i in cat){
               var found = false

               for(j in chipGroupEdit.children){
                   val currChip= j as Chip

                   if(i.name==currChip.text){
                       found=true
                   }
               }

               if(!found)
                   chipGroupEdit.addChip(requireActivity(),i.name)
           }
        }

        //Riempimento dei campi di inserimento con i valori della card
        binding.editName.setText(args.currentCard.name)
        binding.editCategory.setText(args.currentCard.category)
        val ldt = LocalDateTime.parse(args.currentCard.deadline)
        binding.editDate.setText(generateDate(ldt.year, ldt.monthValue, ldt.dayOfMonth, true))
        binding.editTime.setText(generateTime(ldt.hour, ldt.minute))
        dateString = generateDate(ldt.year, ldt.monthValue, ldt.dayOfMonth, false)
        timeString = "${generateTime(ldt.hour, ldt.minute)}:00"

        binding.editDate.setOnFocusChangeListener{ view, isFocused ->
            if(isFocused) {
                view.performClick()
            }
        }

        binding.editDate.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(requireContext(), this, year, month, day).show()
        }

        binding.editTime.setOnFocusChangeListener{ view, isFocused ->
            if(isFocused) {
                view.performClick()
            }
        }

        binding.editTime.setOnClickListener {
            getDateTimeCalendar()
            TimePickerDialog(requireContext(), this, hour, minute, true).show()
        }

        /*
        * Gestione click della chip: se una chip viene cliccata, si prende il suo
        * nome e lo si inserisce nella editText sottostante
        */
        chipGroupEdit.setOnCheckedStateChangeListener { _, checkedIds ->
            if(checkedIds.isNotEmpty()) {
                (chipGroupEdit.findViewById<Chip>(checkedIds[0]))?.let {
                    binding.editCategory.setText(it.text.toString())
                }
            }
            else {
                binding.editCategory.text?.clear()
            }
        }


        /* Gestione inserimento nella tabella category della nuova categoria creata dall'utente*/
        binding.editCategory.setOnKeyListener {_, keyCode,event ->
            if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){

                val textVal=binding.editCategory.text.toString().trim()

                if(existingCat(textVal)) {
                    Toast.makeText(requireContext(), getString(R.string.ExCat), Toast.LENGTH_LONG).show()

                }
                else {
                    if(textVal.isEmpty()){
                        Toast.makeText(requireContext(), getString(R.string.ChooseCat), Toast.LENGTH_LONG).show()
                        binding.editCategory.setText("")
                    }
                    else{
                        /*controllo che la lunghezza della categoria sia adeguata*/
                        if(textVal.length>15){
                            Toast.makeText(requireContext(), getString(R.string.maxNumChar), Toast.LENGTH_LONG).show()
                        }
                        else{
                            chipGroupEdit.addChip(
                                requireActivity(),
                                binding.editCategory.text.toString()
                            )
                            toDoViewModel.addCategory(Category(binding.editCategory.text.toString()))
                            chipGroupEdit.clearCheck()
                        }

                    }
                }
                binding.editCategory.setText("")
            }

            false
        }

        /*controllo che la lunghezza del testo del to-do: se l'utente sfora il numero massimo di caratteri, viene segnalato l'errore*/
        binding.editName.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 50){
                binding.textInputLayoutToDo.error = getString(R.string.maxNumCharName)
            }
            else{
                binding.textInputLayoutToDo.error = null
            }
        }

        /*controllo che la lunghezza della categoria: se l'utente sfora il numero massimo di caratteri, viene segnalato l'errore*/
        binding.editCategory.doOnTextChanged { text, _, _, _ ->
            if(text!!.length > 15){
                binding.editCategoryLayout.error = getString(R.string.maxNumChar)
            }
            else{
                binding.editCategoryLayout.error = null
            }
        }

        /*Gestione dell' evento click sul bottone EDIT che si trova in fondo alla schermata*/
        binding.btnEdit.setOnClickListener{
            if( binding.editName.text.toString().length>50){
                Toast.makeText(requireContext(), getString(R.string.maxNumCharName), Toast.LENGTH_LONG).show()
            }
            if(binding.editCategory.text!!.length>15){
                Toast.makeText(requireContext(), getString(R.string.maxNumChar), Toast.LENGTH_LONG).show()

            }
            else {
                updateItem()
            }
        }


        // Eliminazione CardView con ImageButton a forma di bidone in alto a destra della schermata
        val deleteEditButton : ImageButton = binding.deleteEditButton
        deleteEditButton.setOnClickListener{
            deleteCard()
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

            GlobalScope.launch {
                // crea nuova categoria (nel caso in cui non esista già)
                toDoViewModel.addCategory(Category(category))

                delay(500) // In ms

                // Aggiornamento della card nel database
                toDoViewModel.updateCard(card)
            }

            Toast.makeText(requireContext(), getString(R.string.cardUpdateSucc), Toast.LENGTH_LONG).show()

            // Torna indietro al MainFragment
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
        }
        else{
            Toast.makeText(requireContext(),  getString(R.string.fillAllFields), Toast.LENGTH_LONG).show()
        }


    }
    /**
     * Funzione che controlla che l'utente non abbia lasciato campi di inserimento di testo vuoti
     * @param toDo, testo che descrive l'impegno dell'utente
     * @param category, categoria dell'impegno
     * @param deadline, data e ora dell'impegno
     * @return true se l'input è corretto, false altrimenti
     */
    private fun inputCheck(toDo : String, category: String, deadline : String) : Boolean {
        return !(TextUtils.isEmpty(toDo) || TextUtils.isEmpty(category) || deadline.length != 19)
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
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) { _,_ ->
            toDoViewModel.deleteCard(args.currentCard)
            findNavController().navigate(R.id.action_editFragment_to_mainFragment)
            Toast.makeText(requireContext(), getString(R.string.catRemoveSucc) , Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        val title = R.string.titleDelete
        val message = getString(R.string.mexDelete)
        val nameCard = args.currentCard.name

        builder.setTitle(title)
        builder.setMessage("$message $nameCard")
        builder.setIcon(R.drawable.ic_baseline_delete_24_dark)
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
        for(j in chipGroupEdit.children){
            val currChip= j as Chip
            if(name.equals(currChip.text.toString(), ignoreCase = true)){

                found=true
                break
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

    /**
     * Funzione che riscrive la data ottenuta dal DatePicker nel textfield
     * apposito, e la segna a livello del fragment nella variabile dateString, per
     * inserirla come parametro del to-do al click del pulsante di finalizzazione.
     * La funzione viene chiamata quando "OK" è premuto nel TimePicker.
     *
     * @param p0: DatePicker
     * @param p1: Int - anno scelto nel DatePicker.
     * @param p2: Int - mese scelto nel DatePicker. I DatePicker segnano i mesi da 0 (gennaio) a 11 (dicembre), questa funzione
     * vi sommerà 1 per rendere la data compatibile con il formato pareable da LocalDateTime
     * @param p3: Int - anno scelto nel DatePicker.
     */
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear = p1
        savedMonth = p2+1
        savedDay = p3
        getDateTimeCalendar()
        binding.editDate.setText(generateDate(savedYear, savedMonth, savedDay, true))
        dateString = generateDate(savedYear, savedMonth, savedDay, false)
        Log.d("DATE SET", "set dateString to $dateString")
    }

    /**
     * Funzione che riscrive l'orario ottenuto dal DatePicker nel textfield
     * apposito, e la segna a livello del fragment nella variabile timeString, per
     * inserirla come parametro del to-do al click del pulsante di finalizzazione.
     * La funzione viene chiamata quando "OK" è premuto nel TimePicker.
     * @param p0: TimePicker
     * @param p1: Int - l'ora scelta nel TimePicker.
     * @param p2: Int - il minuto scelto nel TimePicker.
     */
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
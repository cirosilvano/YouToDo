package com.icloud.ciro.silvano.youtodo

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.appbar.MaterialToolbar
import com.icloud.ciro.silvano.youtodo.database.ToDoViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.Card
import com.icloud.ciro.silvano.youtodo.databinding.FragmentMainBinding
import kotlin.math.abs
import kotlin.math.roundToInt

class MainFragment : Fragment(), OnItemSwipeListener, CategoryListener {

    private lateinit var dragHelper: ItemTouchHelper
    private lateinit var swipeHelper: ItemTouchHelper

    private val Int.dp
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(), resources.displayMetrics
        ).roundToInt()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var toDoViewModel : ToDoViewModel
    private lateinit var chipGroupMain: ChipGroup
    private var sharedPreferences: SharedPreferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        TransitionInflater.from(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View {
        //Inflate del layout per il fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        //ViewModel per comunicare con la repository e fornire i dati nella forma desiderata all'interfaccia utente
        toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)

        //Creazione dell'adapter per le cards ed assegnamento di esso alla recyclerView
        val adapter = CardAdapter(this)
        val recyclerView = binding.itemsList
        recyclerView.adapter = adapter

        //Scelta del layout per gli elementi della recyclerView (in questo caso saranno disposti in riga uno sotto l'altro)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Queste due elementi dell'interfaccia sono mostrati quando non sono presenti card per una certa categoria (o una certa vista nell'applicazione)
        sharedPreferences = activity?.getSharedPreferences("themePref", MODE_PRIVATE)
        val btnDarkState = sharedPreferences!!.getBoolean("TOGGLE_DARK", false)
        val ivFreeLight = binding.ivFreeLight
        val ivFreeDark = binding.ivFreeDark
        val tvFree=binding.tvFree

        //Creazione dell'adapter per le categorie
        val adapterCat=CategoryAdapter(this)

        //Creazione dell'observer per mostrare tutte le card presenti nel database all'interno della recyclerView
        //Essendo la lista delle card una LiveData Observer si occuperà in tempo reale di aggiornare il contenuto della recyclerView
        //Il codice all'interno dell'observer sarà eseguito solo se:
        //-gli elementi all'interno del database subiscono modifiche di qualsiasi tipo (aggiunta, rimozione, update)
        //-l'observer passa dallo stato inattivo ad attivo
        toDoViewModel.showAllCards.observe(viewLifecycleOwner) { card ->
            adapter.setData(card) //si setta l'adapter della recyclerView con i nuovi elementi

            //Nel caso in cui non siano presenti elementi, allora si rende visibile il placeholder
            if(adapter.itemCount>0){
                ivFreeDark.isVisible = false
                ivFreeLight.isVisible= false
                tvFree.isVisible=false
            }
            else{
                if(btnDarkState) {
                    ivFreeDark.isVisible = true
                    ivFreeLight.isVisible = false
                }
                else {
                    ivFreeLight.isVisible = true
                    ivFreeDark.isVisible = false
                }
                tvFree.isVisible=true
            }
        }

        //Gestione delle chip che contengono le categorie
        chipGroupMain = binding.chipGroupMain

        toDoViewModel.showAllCategories.observe(viewLifecycleOwner) { cat ->
            adapterCat.setDataCat(cat)

            //Nel caso in cui non siano presenti categorie, vengono inserite le 3 tipologie di default qua sotto
            //Ciò significa che se vengono eliminate tutte le chip, verranno ricreate automaticamente queste 3
            if(adapterCat.itemCount == 0){

                toDoViewModel.addCategory(Category(getString(R.string.all)))
                toDoViewModel.addCategory(Category(getString(R.string.work)))
                toDoViewModel.addCategory(Category(getString(R.string.personal)))
                chipGroupMain.addChip(requireActivity(),getString(R.string.all), adapterCat)
                chipGroupMain.addChip(requireActivity(),getString(R.string.work), adapterCat)
                chipGroupMain.addChip(requireActivity(),getString(R.string.personal), adapterCat)
            }


            //Controllo che verifica se esiste già una chip. Siccome a differenza del database è possibile creare
            //duplicati per le chip, è importante effettuare questa verifica
            for(i in cat) {
                var found = false

                for(j in chipGroupMain.children){
                    val currChip = j as Chip

                    if(i.name == currChip.text)
                        found=true
                }
                if(!found) //se non esistevano già chip con quel nome, allora si può aggiungere
                    chipGroupMain.addChip(requireActivity(),i.name, adapterCat)
            }
        }

        //Questo listener si attiva quando viene cliccata una qualsiasi delle chip
        chipGroupMain.setOnCheckedChangeListener { _, checkedId ->

            //Si seleziona la chip che è stata cliccata
            (chipGroupMain.findViewById<Chip>(checkedId))?.let {

                it.setOnClickListener { chipName ->
                    val myChip:Chip= chipName as Chip //si ottiene il nome della chip che è stata cliccata

                    //Rimozione di tutti gli observers che non riguardano la lista che vogliamo sia osservata
                    toDoViewModel.showAllCards.removeObservers(viewLifecycleOwner)
                    toDoViewModel.showCardsByStatus.removeObservers(viewLifecycleOwner)

                    //Si invoca la select filtrando in base al nome della categoria specificata dalla chip
                    toDoViewModel.setCategory(myChip.text.toString())

                    toDoViewModel.showCardsByCategory.observe(viewLifecycleOwner) { filteredList ->
                        adapter.setData(filteredList)

                        if(adapter.itemCount>0){
                            ivFreeDark.isVisible = false
                            ivFreeLight.isVisible= false
                            tvFree.isVisible=false
                        }
                        else{
                            if(btnDarkState) {
                                ivFreeDark.isVisible = true
                                ivFreeLight.isVisible = false
                            }
                            else {
                                ivFreeLight.isVisible = true
                                ivFreeDark.isVisible = false
                            }
                            tvFree.isVisible=true
                        }
                    }
                }
           }
        }

        binding.addFAB.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFragment)
        }

        //Gestione dei click per i vari tasti del menù
        val bottomAppBar: BottomNavigationView = binding.bottomNavigationView

        bottomAppBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settings_nav->{
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                }

                R.id.category_nav->{
                    val action = MainFragmentDirections.actionMainFragmentToCategoryFragment()
                    findNavController().navigate(action)
                }

            }
            true

        }

        //Gestione del click delle opzioni del menù a tendina che compare premendo i 3 punti in alto a destra
        val topAppBar:MaterialToolbar=binding.topAppBar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                //Questo tasto mostra tutte le card che sono state create
                R.id.all_nav -> {
                    toDoViewModel.showAllCards.removeObservers(viewLifecycleOwner)
                    toDoViewModel.showCardsByStatus.removeObservers(viewLifecycleOwner)
                    toDoViewModel.showCardsByCategory.removeObservers(viewLifecycleOwner)

                    toDoViewModel.showAllCards.observe(viewLifecycleOwner) { toDoList ->
                        adapter.setData(toDoList)

                        if(adapter.itemCount>0){
                            ivFreeDark.isVisible = false
                            ivFreeLight.isVisible= false
                            tvFree.isVisible=false
                        }
                        else{
                            if(btnDarkState) {
                                ivFreeDark.isVisible = true
                                ivFreeLight.isVisible = false
                            }
                            else {
                                ivFreeLight.isVisible = true
                                ivFreeDark.isVisible = false
                            }
                            tvFree.isVisible=true
                        }
                    }
                    true
                }
                //Questo tasto mostra tutte le card che non sono ancora state completate (la check non è spuntata)
                R.id.to_do_nav -> {
                    toDoViewModel.showAllCards.removeObservers(viewLifecycleOwner)
                    toDoViewModel.showCardsByCategory.removeObservers(viewLifecycleOwner)

                    //Si invoca la select filtrando in base allo stato delle cards (in questo caso sono restituite quelle non checkate)
                    toDoViewModel.setDone(0)
                    toDoViewModel.showCardsByStatus.observe(viewLifecycleOwner) { toDoList ->
                        adapter.setData(toDoList)

                        if(adapter.itemCount>0){
                            ivFreeDark.isVisible = false
                            ivFreeLight.isVisible= false
                            tvFree.isVisible=false
                        }
                        else{
                            if(btnDarkState) {
                                ivFreeDark.isVisible = true
                                ivFreeLight.isVisible = false
                            }
                            else {
                                ivFreeLight.isVisible = true
                                ivFreeDark.isVisible = false
                            }
                            tvFree.isVisible=true
                        }
                    }
                    true
                }

                //Questo tasto mostra tutte le card che sono state completate (è stata spuntata la check)
                R.id.done_nav -> {
                    toDoViewModel.showAllCards.removeObservers(viewLifecycleOwner)
                    toDoViewModel.showCardsByCategory.removeObservers(viewLifecycleOwner)

                    //Si invoca la select filtrando in base allo stato delle cards (in questo caso sono restituite quelle checkate)
                    toDoViewModel.setDone(1)
                    toDoViewModel.showCardsByStatus.observe(viewLifecycleOwner) { doneList ->
                        adapter.setData(doneList)

                        if(adapter.itemCount>0){
                            ivFreeDark.isVisible = false
                            ivFreeLight.isVisible= false
                            tvFree.isVisible=false
                        }
                        else{
                            if(btnDarkState) {
                                ivFreeDark.isVisible = true
                                ivFreeLight.isVisible = false
                            }
                            else {
                                ivFreeLight.isVisible = true
                                ivFreeDark.isVisible = false
                            }
                            tvFree.isVisible=true
                        }
                    }
                    true
                }

                else -> false
            }
        }

        //define swipe helper here
        swipeHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            //grandezza-410
            private val limitScrollX = dipToPx(140f, binding.itemsList.context)
            private var currentScrollX = 0
            private var currentScrollXWhenInActive = 0
            private var initWhenActive = 0f
            private var firstInActive = false

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
                return Integer.MAX_VALUE.toFloat()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if(dX == 0f) {
                        currentScrollX = viewHolder.itemView.scrollX
                        firstInActive = true
                    }

                    if(isCurrentlyActive) {
                        //swipe with finger
                        var scrollOffset = currentScrollX + (-dX).toInt()

                        if(scrollOffset > limitScrollX)
                            scrollOffset = limitScrollX
                        else
                            if(scrollOffset < 0)
                                scrollOffset = 0

                        viewHolder.itemView.scrollTo(scrollOffset, 0)
                    }
                    else {
                        //swipe with auto animation
                        if(firstInActive) {
                            firstInActive = false
                            currentScrollXWhenInActive = viewHolder.itemView.scrollX
                            initWhenActive = dX
                        }

                        if(viewHolder.itemView.scrollX < limitScrollX) {
                            viewHolder.itemView.scrollTo((currentScrollXWhenInActive * dX / initWhenActive).toInt(), 0)
                        }
                    }
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                if(viewHolder.itemView.scrollX > limitScrollX) {
                    viewHolder.itemView.scrollTo(limitScrollX, 0)
                }
                else
                    if(viewHolder.itemView.scrollX < 0) {
                        viewHolder.itemView.scrollTo(0, 0)
                    }
            }
        })

        swipeHelper.attachToRecyclerView(recyclerView)

        return binding.root
}//OnCreateView


private fun dipToPx(dipValue: Float, context: Context) : Int {
    return (dipValue * context.resources.displayMetrics.density).toInt()
}

// create chip programmatically and add it to chip group
private fun ChipGroup.addChip(context: Context?, label: String, adapter : CategoryAdapter){
 Chip(context).apply {
     id = View.generateViewId()
     text = label
     isClickable = true
     isCheckable = true
     isCheckedIconVisible = true
     isFocusable = true
     addView(this)
     this.setOnCloseIconClickListener{
         val previous = adapter.itemCount
         var success = 0
         //Eliminazione dell'elemento dalla tabella
         try {
             Log.d("", this.text.toString())
             toDoViewModel.deleteCategory(Category(this.text.toString().trim()))
             success = adapter.itemCount
         } catch(e : android.database.sqlite.SQLiteConstraintException) {
             Toast.makeText(requireContext(), "Impossibile eliminare la category perché ci sono card con quella.  ", Toast.LENGTH_LONG).show()
         }

         if(success == previous - 1) {
             //Rimozione della chip
             removeView(this)
         }
     }
 }
}

/**
* Funzione che modifica lo stato della card di cui si è premuta la check
* @param card card di cui si vuole cambiare lo stato (da checked a unchecked o viceversa)
*/
override fun onCheckCardClick(card: Card) {
 //Si verifica lo stato della card e si invoca l'update con lo stato opposto

 if(card.isDone)
     toDoViewModel.updateCard(Card(card.id, card.name, card.category, card.deadline, false))
 else
     toDoViewModel.updateCard(Card(card.id, card.name, card.category, card.deadline, true))
}

/**
* Funzione che triggera il messaggio di eliminazione nel caso in cui venga fatto uno swipe sulla card
* @param card card su cui è stato lo swipe e per la quale si propone la cancellazione
*/
override fun onCardSwipe(card: Card) {
 val builder = AlertDialog.Builder(requireContext())
 builder.setPositiveButton(R.string.yes) { _,_ ->
     toDoViewModel.deleteCard(card)
     Toast.makeText(requireContext(), "Deleted ${card.name}!", Toast.LENGTH_LONG).show()
 }

 builder.setNegativeButton("No") { _,_ ->

 }

 builder.setTitle("Are you sure you want to delete ${card.name} ?")
 builder.setMessage("Are you sure you want to delete ${card.name} ?")

 builder.create().show()
}

override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
 inflater.inflate(R.menu.menu_filter,menu)
 super.onCreateOptionsMenu(menu, inflater)
}

override fun categoryEdit(category: Category) {}

override fun categoryDelete(category: Category) {}

}
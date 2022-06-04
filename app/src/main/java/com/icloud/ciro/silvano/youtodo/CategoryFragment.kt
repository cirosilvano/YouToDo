package com.icloud.ciro.silvano.youtodo

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        //Inflate del layout per il fragment
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        //Creazione del riferimento al bottone "Indietro" che servirà a portare nel MainFragment
        val btnBack = binding.backCategoryButton

        //Gestione dell'evento di click nel bottone indietro (la freccia in altro a sinistra della schermata)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_mainFragment)
        }

        //Creazione dell'adapter per le category ed assegnamento di esso alla recyclerView
        val adapterCat=CategoryAdapter(this)
        val recyclerView = binding.catList
        recyclerView.adapter = adapterCat

        //Scelta del layout per gli elementi della recyclerView (saranno disposti in riga uno sotto l'altro)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //ViewModel per comunicare con la repository
        toDoViewModel = ViewModelProvider(this).get(ToDoViewModel::class.java)


        /*
        * Creazione dell'observer per mostrare tutte le categorie presenti nel database all'interno della recyclerView
        * Essendo la lista delle categorie una LiveData Observer si occuperà in tempo reale di aggiornare il contenuto della recyclerView
        * Il codice all'interno dell'observer sarà eseguito solo se:
        * -gli elementi all'interno del database subiscono modifiche di qualsiasi tipo (aggiunta, rimozione, update)
        * -l'observer passa dallo stato inattivo ad attivo
        */
        toDoViewModel.showAllCategories.observe(viewLifecycleOwner, Observer { cat ->
            adapterCat.setDataCat(cat)//si setta l'adapter della recyclerView con i nuovi elementi
        })

        /*
        * Gestione FAB per aggiungere una nuova categoria:
        * Al click del FAB apparira un DialogFragment
        */
        binding.FabAddCat.setOnClickListener {
            val catDialog = CategoryAddDialog()
            catDialog.show(parentFragmentManager, "")
        }

        return binding.root
    }

    /*
        * Gestione bottone edit (icona a forma di penna) per modificare una nuova categoria:
        * Per non dover gestire un altro Fragment contenente solamente una textfield e un bottone
        * si è optato per questa strada. Al click della penna vegono rese visibili le componenti
        * che servono ad aggiungere una nuova categorie, rendendo invisibile quelle principali.
        * Si ha lo stesso effetto che si avrebbe con l'aggiunta di un fragment.
        * */

    /**Funzione per la gestione della modifica della categoria
     * @param category la categoria che si vuole modificare individuata dal click del bottone a forma di matita
     */
    override fun categoryEdit(category: Category) {
        val catDialog = CategoryEditDialog(category)

        catDialog.show(parentFragmentManager, "")
    }

    /**Funzione per la gestione dell'eliminazione della categoria
     * @param category la categoria che si vuole eliminare individuata dal click del bottone a forma di bidone
     */
    override fun categoryDelete(category: Category) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setPositiveButton(getString(R.string.yes)) { _,_ ->
            try {
                toDoViewModel.deleteCategory(category)
            } catch(e : Exception) {
                Toast.makeText(requireContext(), "Impossibile eliminarla !TOdo sotto tale categoria", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(requireContext(), getString(R.string.catRemoveSucc), Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        var title=R.string.titleDelete
        var message=getString(R.string.mexDelete)
        var nameCat=category.name

        builder.setTitle(title)
        builder.setMessage(message+" "+nameCat)
        builder.setIcon(R.drawable.ic_baseline_delete_24_dark)
        builder.create().show()
    }
}
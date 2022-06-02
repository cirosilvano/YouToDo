package com.icloud.ciro.silvano.youtodo

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        //Creazione del riferimento al bottone "Indientro" che servirà a portare nel MainFragment
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
        * Per non dover gestire un altro Fragment contenente solamente una textfield e un bottone
        * si è optato per questa strada. Al click del FAB vegono rese visibili le componenti
        * che servono ad aggiungere una nuova categorie, rendendo invisibile quelle principali.
        * Si ha lo stesso effetto che si avrebbe con l'aggiunta di un fragment.
        * */
        binding.FabAddCat.setOnClickListener{
            binding.txtTitleActionCat.setText(R.string.addCatTitle)
            binding.catList.isVisible=false
            binding.txtTitleActionCat.isVisible=true
            binding.txtFieldCategory.isVisible=true
            binding.btnBackMainCat.isVisible=true
            binding.backCategoryButton.isVisible=false
            binding.FabAddCat.isVisible=false

            binding.btnBackMainCat.setOnClickListener{
                binding.txtTitleActionCat.isVisible=false
                binding.txtFieldCategory.isVisible=false
                binding.catList.isVisible=true
                binding.txtFieldCategory.setText("")
                binding.btnBackMainCat.isVisible=false
                binding.backCategoryButton.isVisible=true
                binding.FabAddCat.isVisible=true
            }
            binding.txtFieldCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
                if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){

                    var newVal=binding.txtFieldCategory.text.toString().trim().lowercase()
                    if(!newVal.isEmpty()){
                        val previous = adapterCat.itemCount
                        var now : Int = 0
                        /*
                        * La funzione addCatLong serve ad aggiungere una nuova categorie nella tabella category.
                        * Nel momento in cui una nuova categoria proposta dall'utente risulta già inserita nella tabella, tale
                        * funzione ritornerà un numero di righe "moficate" pari a zero e perciò l'utente sarà avvisato con il relativo messaggio*/
                        toDoViewModel.addCatLong(Category(newVal))
                        now = adapterCat.itemCount
                        if(now == previous - 1){
                            Toast.makeText(requireContext(), "Successfully Added!", Toast.LENGTH_LONG).show()
                        }
                        else{
                            Toast.makeText(requireContext(), "Existing category!", Toast.LENGTH_LONG).show()
                        }
                        binding.txtTitleActionCat.isVisible=false
                        binding.txtFieldCategory.isVisible=false
                        binding.btnBackMainCat.isVisible=false
                        binding.catList.isVisible=true
                        binding.backCategoryButton.isVisible=true
                        binding.FabAddCat.isVisible=true
                        binding.txtFieldCategory.setText("")
                    }
                    else{
                        Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
                    }
                    true
                }

                false
            })
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
        binding.txtTitleActionCat.setText(R.string.editCatTitle)
        binding.txtFieldCategory.setText(category.name.toString())
        var oldName=category.name.toString()
        binding.btnBackMainCat.isVisible=true
        binding.catList.isVisible=false
        binding.txtTitleActionCat.isVisible=true
        binding.txtFieldCategory.isVisible=true
        binding.backCategoryButton.isVisible=false
        binding.FabAddCat.isVisible=false
        binding.btnBackMainCat.setOnClickListener{
            binding.txtTitleActionCat.isVisible=false
            binding.txtFieldCategory.isVisible=false
            binding.catList.isVisible=true
            binding.txtFieldCategory.setText("")
            binding.btnBackMainCat.isVisible=false
            binding.backCategoryButton.isVisible=true
            binding.FabAddCat.isVisible=true
        }

        binding.txtFieldCategory.setOnKeyListener(View.OnKeyListener{v, keyCode,event ->
            if(event.action== KeyEvent.ACTION_UP && keyCode== KeyEvent.KEYCODE_ENTER ){

                var newName=binding.txtFieldCategory.text.toString().trim().lowercase()
                if(!newName.isEmpty()){
                    toDoViewModel.updateCategory(oldName,newName)
                    Toast.makeText(requireContext(), "Successfully updated!", Toast.LENGTH_LONG).show()
                    binding.txtTitleActionCat.isVisible=false
                    binding.txtFieldCategory.isVisible=false
                    binding.btnBackMainCat.isVisible=false
                    binding.catList.isVisible=true
                    binding.backCategoryButton.isVisible=true
                    binding.FabAddCat.isVisible=true
                    binding.txtFieldCategory.setText("")
                }
                else{
                    Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
                }
                true
            }

            false
        })
    }

    /**Funzione per la gestione dell'eliminazione della categoria
     * @param category la categoria che si vuole eliminare individuata dal click del bottone a forma di bidone
     */
    override fun categoryDelete(category: Category) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _,_ ->
            try {
                toDoViewModel.deleteCategory(category)
            } catch(e : Exception) {
                Toast.makeText(requireContext(), "Impossibile eliminarla !TOdo sotto tale categoria", Toast.LENGTH_LONG).show()
            }
            Toast.makeText(requireContext(), "category successfully removed !", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        builder.setTitle("Are you sure you want to delete ${category.name} ?")
        builder.setMessage("Are you sure you want to delete ${category.name} ?")

        builder.create().show()
    }
}
package com.icloud.ciro.silvano.youtodo

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.appbar.MaterialToolbar
import com.icloud.ciro.silvano.youtodo.database.ItemViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.Item
import com.icloud.ciro.silvano.youtodo.databinding.FragmentMainBinding

class MainFragment : Fragment(), OnItemSwipeListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var itemViewModel : ItemViewModel
    private  lateinit var ivFree:ImageView
    private lateinit var tvFree: TextView
    private lateinit var currentCat:Category
    private lateinit var chipGroupMain: ChipGroup
    private  var categoryList:List<Category>?=null
    lateinit var todoRecyclerView: RecyclerView
    private var mainActivity=getActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.fade)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment

    Log.d("","mainActivity: ${mainActivity}")
         _binding = FragmentMainBinding.inflate(inflater, container, false)
        chipGroupMain=binding.chipGroupMain
        val adapter = ItemAdapter(this)
        val recyclerView = binding.itemsList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        val adapterCat=CategoryAdapter{
            currentCat=it
        }
       /*val recyclerViewCat=binding.catList
        recyclerViewCat.adapter=adapterCat
        recyclerViewCat.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)*/


        var ivFree= binding.ivFree
        var tvFree=binding.tvFree
        //ItemViewModel
        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)


        itemViewModel.showAllItems.observe(viewLifecycleOwner, Observer{ card ->
            adapter.setData(card)

            if(adapter.itemCount>0){
                ivFree.isVisible=false
                tvFree.isVisible=false

            }
            else{
                ivFree.isVisible=true
                tvFree.isVisible=true
            }

        })

        

        /*Gestione delle chip che contengono le categorie*/

        itemViewModel.showAllCategories.observe(viewLifecycleOwner,Observer { cat ->
            adapterCat.setDataCat(cat)

            if(adapterCat.itemCount==0){
                itemViewModel.addCategory(Category("Tutti"))
                itemViewModel.addCategory(Category("Lavoro"))
                itemViewModel.addCategory(Category("Personale"))
                chipGroupMain.addChip(requireActivity(),"Tutti")
                chipGroupMain.addChip(requireActivity(),"Lavoro")
                chipGroupMain.addChip(requireActivity(),"Personale")
            }

            for(i in cat){
                var found:Boolean=false
                for(j in chipGroupMain.children){
                    var currChip= j as Chip
                    if(i.name==currChip.text){
                        found=true
                    }
                }
                if(!found)
                    chipGroupMain.addChip(requireActivity(),i.name)
            }


        })


        // set chip group checked change listener
       chipGroupMain.setOnCheckedChangeListener { _, checkedId ->
            // get the checked chip instance from chip group
           (chipGroupMain.findViewById<Chip>(checkedId))?.let {
                // Show the checked chip text on text view
                it.setOnClickListener {
                    var myChip:Chip=it as Chip

                    //Remove all observers
                    itemViewModel.showAllItems.removeObservers(viewLifecycleOwner)
                    itemViewModel.showItemsDone.removeObservers(viewLifecycleOwner)

                    itemViewModel.setCategory(myChip.text.toString())
                    itemViewModel.showItemsCategory.observe(viewLifecycleOwner, Observer { filteredList ->
                        adapter.setData(filteredList)

                        if(adapter.itemCount>0){
                            ivFree.isVisible=false
                            tvFree.isVisible=false

                        }
                        else{
                            ivFree.isVisible=true
                            tvFree.isVisible=true
                        }
                    })
                }
           }
       }

        binding.addFAB.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addFragment)
        }



        val bottomAppBar: BottomNavigationView = binding.bottomNavigationView

        bottomAppBar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.all_nav -> {
                    //Remove all observers
                    itemViewModel.showAllItems.removeObservers(viewLifecycleOwner)
                    itemViewModel.showItemsDone.removeObservers(viewLifecycleOwner)
                    itemViewModel.showItemsCategory.removeObservers(viewLifecycleOwner)

                    itemViewModel.showAllItems.observe(viewLifecycleOwner, Observer { toDoList ->
                        adapter.setData(toDoList)

                        if(adapter.itemCount>0){
                            ivFree.isVisible=false
                            tvFree.isVisible=false

                        }
                        else{
                            ivFree.isVisible=true
                            tvFree.isVisible=true
                        }
                    })
                    true
                }

                R.id.to_do_nav -> {
                    //Remove all observers
                    itemViewModel.showAllItems.removeObservers(viewLifecycleOwner)
                    itemViewModel.showItemsCategory.removeObservers(viewLifecycleOwner)

                    itemViewModel.setDone(0)
                    itemViewModel.showItemsDone.observe(viewLifecycleOwner, Observer { toDoList ->
                        adapter.setData(toDoList)

                        if(adapter.itemCount>0){
                            ivFree.isVisible=false
                            tvFree.isVisible=false

                        }
                        else{
                            ivFree.isVisible=true
                            tvFree.isVisible=true
                        }
                    })
                    true
                }

                R.id.done_nav -> {
                    //Remove all observers
                    itemViewModel.showAllItems.removeObservers(viewLifecycleOwner)
                    itemViewModel.showItemsCategory.removeObservers(viewLifecycleOwner)

                    itemViewModel.setDone(1)
                    itemViewModel.showItemsDone.observe(viewLifecycleOwner, Observer { doneList ->
                        adapter.setData(doneList)

                        if(adapter.itemCount>0){
                            ivFree.isVisible=false
                            tvFree.isVisible=false

                        }
                        else{
                            ivFree.isVisible=true
                            tvFree.isVisible=true
                        }
                    })
                    true
                }

                R.id.settings_nav -> {
                    // Handle search icon press
                    findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                    true
                }
                else -> false
            }
        }

        var topAppBar:MaterialToolbar=binding.topAppBar
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.GestCat -> {
                    val action = MainFragmentDirections.actionMainFragmentToCategoryFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }

        return binding.root
    }//OnCreateView

    // create chip programmatically and add it to chip group
    private fun ChipGroup.addChip(context: Context?, label: String){
        Chip(context).apply {
            id = View.generateViewId()
            text = label
            isClickable = true
            isCheckable = true
            isCheckedIconVisible = true
            isCloseIconVisible = true
            isFocusable = true
            addView(this)
            this.setOnCloseIconClickListener{
                var success : Int = 0
                //Eliminazione dell'elemento dalla tabella
                try {
                    success = itemViewModel.deleteCategory(Category(this.text.toString()))
                } catch(e : android.database.sqlite.SQLiteConstraintException) {
                    Toast.makeText(requireContext(), "Impossibile eliminare la category perché ci sono card con quella.  ", Toast.LENGTH_LONG).show()
                }
                if(success > 0) {
                    //Rimozione della chip
                    removeView(this)
                }
            }
        }
    }

    override fun onItemTouchCheck(item: Item) {
        if(item.isDone)
            itemViewModel.updateItem(Item(item.id, item.name, item.category, item.deadline, false))
        else
            itemViewModel.updateItem(Item(item.id, item.name, item.category, item.deadline, true))
    }

    override fun onItemSwipe(item: Item) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _,_ ->
            itemViewModel.deleteItem(item)
            Toast.makeText(requireContext(), "Successfully removed ${item.name}!", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { _,_ ->

        }

        builder.setTitle("Are you sure you want to delete ${item.name} ?")
        builder.setMessage("Are you sure you want to delete ${item.name} ?")

        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_category,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.GestCat){
            val action = MainFragmentDirections.actionMainFragmentToCategoryFragment()
            findNavController().navigate(action)
        }
        if(id==R.id.toDoOrder){
            Toast.makeText(requireContext(), "Ordinamento categorie!", Toast.LENGTH_LONG).show()
        }
        return super.onOptionsItemSelected(item)
    }

}
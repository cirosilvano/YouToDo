package com.icloud.ciro.silvano.youtodo.database

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/*classe ItemViewModel
* ha il compito di fornire i dati alla UI e i cambiamenti di configurazione
* Essa serve come intermediario fra la repository e la UI
*/
class ItemViewModel(application: Application): AndroidViewModel(application) {

    val showAllItems: LiveData<List<Item>>
    val showAllCategories: LiveData<List<Category>>
    private val repository: ItemRepository

    private val filterLiveDataDone = MutableLiveData<Int>()
    val showItemsDone: LiveData<List<Item>>

    private val filterLiveDataCategory = MutableLiveData<String>()
    val showItemsCategory: LiveData<List<Item>>


    init {
        val itemDao = ItemDatabase.getDatabase(application).itemDao()
        val categoryDao = ItemDatabase.getDatabase(application).categoryDao()
        repository = ItemRepository(itemDao, categoryDao)
        showAllItems = repository.readAllItemsData
        showAllCategories = repository.readAllCategoryData

        showItemsDone = Transformations.switchMap(filterLiveDataDone) { v -> Int
            repository.selectItemsDone(v)
        }

        showItemsCategory = Transformations.switchMap(filterLiveDataCategory) { s -> String
            repository.selectFilteredItems(s)
        }
    }

    fun setDone(done: Int) {
        filterLiveDataDone.value = done
    }

    fun setCategory(filter: String) {
        filterLiveDataCategory.value = filter
    }

    fun addItem(item: Item){
        //Dispacthers.IO significa che la query è eseguita in background
        viewModelScope.launch(Dispatchers.IO) {
            repository.addItem(item)
        }
    }

    fun updateItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteItem(item)
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCategory(category)
        }
    }
    fun deleteCategory(category: Category) : Int{
        return repository.deleteCategory(category)
    }
}
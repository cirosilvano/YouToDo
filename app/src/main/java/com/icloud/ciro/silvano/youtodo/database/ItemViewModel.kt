package com.icloud.ciro.silvano.youtodo.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.icloud.ciro.silvano.youtodo.database.ItemDatabase
import com.icloud.ciro.silvano.youtodo.database.ItemRepository
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

    init {
        val itemDao = ItemDatabase.getDatabase(application).itemDao()
        val categoryDao = ItemDatabase.getDatabase(application).categoryDao()
        repository = ItemRepository(itemDao, categoryDao)
        showAllItems = repository.readAllItemsData
        showAllCategories = repository.readAllCategoryData
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
    fun deleteCategory(category: Category){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }

    fun showAllCategoriesList():List<Category>{
        return repository.showAllCategoriesList()
    }
}
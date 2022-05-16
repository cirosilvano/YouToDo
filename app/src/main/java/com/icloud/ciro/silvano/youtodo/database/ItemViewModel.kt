package com.icloud.ciro.silvano.youtodo.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.icloud.ciro.silvano.youtodo.database.ItemDatabase
import com.icloud.ciro.silvano.youtodo.database.ItemRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(application: Application): AndroidViewModel(application) {

    val showAllItems: LiveData<List<Item>>
    private val repository: ItemRepository

    init {
        val userDao = ItemDatabase.getDatabase(application).itemDao()
        repository = ItemRepository(userDao)
        showAllItems = repository.readAllData
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
}
package com.example.youtodo.databaseUtilities

import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao: ItemDao) {

    val readAllData: LiveData<List<Item>> = itemDao.showAllItems()

    fun addItem(item: Item){
        itemDao.addItem(item)
    }

    fun updateItem(item: Item){
        itemDao.updateItem(item)
    }

    fun deleteItem(item: Item){
        itemDao.deleteItem(item)
    }
}
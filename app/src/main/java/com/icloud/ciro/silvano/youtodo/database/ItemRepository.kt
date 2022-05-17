package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData
import com.icloud.ciro.silvano.youtodo.database.ItemDao

/*Classe ItemRepository
* classe che astrae l'accesso all'insieme dei dati del database
*/
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
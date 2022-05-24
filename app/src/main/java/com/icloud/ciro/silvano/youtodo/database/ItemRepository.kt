package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData
import com.icloud.ciro.silvano.youtodo.database.ItemDao

/*Classe ItemRepository
* classe che astrae l'accesso all'insieme dei dati del database
*/
class ItemRepository(private val itemDao: ItemDao, private val categoryDao: CategoryDao) {

    val readAllItemsData: LiveData<List<Item>> = itemDao.showAllItems()
    val readAllCategoryData:LiveData<List<Category>> = categoryDao.showAllCategories()

    fun addItem(item: Item){
        itemDao.addItem(item)
    }

    fun updateItem(item: Item){
        itemDao.updateItem(item)
    }

    fun deleteItem(item: Item){
        itemDao.deleteItem(item)
    }

    fun addCategory(category: Category) {
        categoryDao.addCategory(category)
    }

    fun deleteCategory(category: Category) : Int {
        return categoryDao.deleteCategory(category)
    }

    fun selectFilteredItems(name : String) : LiveData<List<Item>> {
        return itemDao.selectFilteredItems(name)
    }

    fun selectItemsDone() : LiveData<List<Item>> {
        return itemDao.selectItemsDone()
    }

    fun selectItemsToDo() : LiveData<List<Item>> {
        return itemDao.selectItemsToDo()
    }
}
package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData
import androidx.room.*

/*Interfaccia ItemDao
* Contiene tutti i metodi utilizzati per accedere alle tabelle de database
*/
@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addItem(item: Item)

    @Update
    fun updateItem(item: Item)

    @Delete
    fun deleteItem(item: Item)

    @Query("SELECT * FROM item")
    fun showAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE category = :category")
    fun selectFilteredItems(category: String): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE isDone = 1")
    fun selectItemsDone() : LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE isDone = 0")
    fun selectItemsToDo() : LiveData<List<Item>>
}
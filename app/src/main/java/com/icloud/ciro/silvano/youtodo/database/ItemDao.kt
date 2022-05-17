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

}
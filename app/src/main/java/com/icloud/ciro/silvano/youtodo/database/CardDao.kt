package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query

/*Interfaccia ItemDao
* Contiene tutti i metodi utilizzati per accedere alle tabella item del database
*/
@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCard(card: Card)

    @Update
    fun updateCard(card: Card)

    @Delete
    fun deleteCard(card: Card)

    @Query("SELECT * FROM card")
    fun showAllCards(): LiveData<List<Card>>

    @Query("SELECT * FROM card WHERE category = :category")
    fun selectCardsByCategory(category: String): LiveData<List<Card>>

    @Query("SELECT * FROM card WHERE isDone = :done")
    fun selectCardsByStatus(done : Int) : LiveData<List<Card>>
}
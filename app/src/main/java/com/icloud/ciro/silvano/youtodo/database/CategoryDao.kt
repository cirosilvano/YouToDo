package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData
import androidx.room.*

/*Interfaccia ItemDao
* Contiene tutti i metodi utilizzati per accedere alle tabella category del database
*/
@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCatLong(category: Category):Long

    @Delete
    fun deleteCategory(category: Category) : Int

    @Query("SELECT * FROM category")
    fun showAllCategories(): LiveData<List<Category>>

    @Query("UPDATE category SET name = :newName WHERE name = :oldName ")
    fun updateCategory(oldName:String,newName:String)
}
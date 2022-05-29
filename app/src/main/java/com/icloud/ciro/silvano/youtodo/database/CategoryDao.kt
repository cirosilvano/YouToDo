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

    @Delete
    fun deleteCategory(category: Category) : Int

    @Update
    fun updateCategory(category: Category)

    @Query("SELECT * FROM category")
    fun showAllCategories(): LiveData<List<Category>>
}
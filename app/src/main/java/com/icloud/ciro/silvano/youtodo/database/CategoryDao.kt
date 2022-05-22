package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCategory(category: Category)

    @Delete
    fun deleteCategory(category: Category)

   /* @Update
    fun updateCategory(category: Category)*/

    @Query("SELECT * FROM category")
    fun showAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM category")
    fun showAllCategoriesList(): List<Category>


}
package com.icloud.ciro.silvano.youtodo.database

import androidx.lifecycle.LiveData

/*Classe ItemRepository
* classe che astrae l'accesso all'insieme dei dati del database
*/
class ToDoRepository(private val cardDao: CardDao, private val categoryDao: CategoryDao) {

    val readAllCardsData: LiveData<List<Card>> = cardDao.showAllCards()
    val readAllCategoryData:LiveData<List<Category>> = categoryDao.showAllCategories()

    fun addCard(card: Card){
        cardDao.addCard(card)
    }

    fun updateCard(card: Card){
        cardDao.updateCard(card)
    }

    fun deleteCard(card: Card){
        cardDao.deleteCard(card)
    }

    fun addCategory(category: Category) {
        categoryDao.addCategory(category)
    }

    fun deleteCategory(category: Category) : Int {
        return categoryDao.deleteCategory(category)
    }

    fun selectCardsByCategory(name : String) : LiveData<List<Card>> {
        return cardDao.selectCardsByCategory(name)
    }

    fun selectCardsByStatus(done : Int) : LiveData<List<Card>> {
        return cardDao.selectCardsByStatus(done)
    }
}
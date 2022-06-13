package com.icloud.ciro.silvano.youtodo.database

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


/*classe ItemViewModel
* ha il compito di fornire i dati alla UI e i cambiamenti di configurazione
* Essa serve come intermediario fra la repository e la UI
*/
class ToDoViewModel(application: Application): AndroidViewModel(application) {
    private val repository: ToDoRepository

    val showAllCards: LiveData<List<Card>>
    val showAllCategories: LiveData<List<Category>>

    private val filterLiveDataByStatus = MutableLiveData<Int>()
    val showCardsByStatus: LiveData<List<Card>>

    private val filterLiveDataByCategory = MutableLiveData<String>()
    val showCardsByCategory: LiveData<List<Card>>

    init {
        val cardDao = ToDoDatabase.getDatabase(application).cardDao()
        val categoryDao = ToDoDatabase.getDatabase(application).categoryDao()
        repository = ToDoRepository(cardDao, categoryDao)
        showAllCards = repository.readAllCardsData
        showAllCategories = repository.readAllCategoryData

        showCardsByStatus = Transformations.switchMap(filterLiveDataByStatus) { v -> Int
            repository.selectCardsByStatus(v)
        }

        showCardsByCategory = Transformations.switchMap(filterLiveDataByCategory) { s -> String
            repository.selectCardsByCategory(s)
        }
    }

    fun setDone(done: Int) {
        filterLiveDataByStatus.value = done
    }

    fun setCategory(filter: String) {
        filterLiveDataByCategory.value = filter
    }

    fun addCard(card: Card){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCard(card)
        }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCard(card)
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCard(card)
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }

    fun updateCategory(oldName:String,newName:String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(oldName, newName)
        }
    }
}
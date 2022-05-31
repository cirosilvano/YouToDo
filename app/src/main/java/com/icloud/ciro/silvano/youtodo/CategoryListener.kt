package com.icloud.ciro.silvano.youtodo

import com.icloud.ciro.silvano.youtodo.database.Category
/**
 * Interfaccia che permette di comunicare l'elemento corrente selezionato
 * nella RecyclerView contenuta nel Fragment che implementerà tale interfaccia.
 * Essa consente al Fragment di ottenere l'elemento selezionato nella RecyclerView direttamente
 * dall'holder che provvederà a passarlo come parametro alla funzione dell'interfaccia.
 */
interface CategoryListener  {

    /**Funzione per la gestione della modifica della categoria
     * @param category la categoria che si vuole modificare
     */
    fun categoryEdit(category: Category)

    /**Funzione per la gestione dell'eliminazione della categoria
     * @param category la categoria che si vuole eliminare
     */
    fun categoryDelete(category: Category)
}
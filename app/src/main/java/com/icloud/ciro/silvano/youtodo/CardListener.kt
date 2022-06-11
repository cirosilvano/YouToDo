package com.icloud.ciro.silvano.youtodo

import com.icloud.ciro.silvano.youtodo.database.Card

/**
 * Interfaccia che permette di individuare la card selezionata
 * nella RecyclerView contenuta nel Fragment che implementerà tale interfaccia.
 * In base al tipo di azione compiuto con la card specifica, sarà quindi possibile definire un
 * determinato comportamento per gestire l'evento (dando la possibiltà quindi di utilizzare il
 * ViewModel e di modificare il database)
 */
interface CardListener {
    /**
     * Funzione per la gestione del longTouch della card
     * @param card la card su cui si è tenuto premuto
     */
    fun onLongCardClick(card: Card)

    /**
     * Funzione per la gestione
     */
    fun onCardSwipe(card: Card)
}
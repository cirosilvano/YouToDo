package com.icloud.ciro.silvano.youtodo

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Classe che definisce la CallBack usata dall'ItemTouchHelper
 * @param context context usato per ottenere la densità dello schermo. Questa serve per determinare di quanto trascinare la card per rilevare lo swipe
 */
class ToDoItemTouchHelper(context: Context?): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    //questa variabile specifica di quanto bisogna scrollare perché lo swipe vada a buon
    //fine (se l'utente scrolla troppo poco la card viene riportata alla posizione originale
    //e il "menu extra" viene rinascosto
    private val limitScrollX = (140f * context!!.resources.displayMetrics.density).toInt()

    //specifica quanto l'utente ha scrollato a sinistra
    private var currentScrollX = 0
    private var currentScrollXWhenInactive = 0
    private var initWhenActive = 0f
    private var firstInactive = false

    /**
     * Questa funzione è utilizzata nel caso in cui si supporti il drag and drop
     * per spostare una elemento dalla vecchia posizione alla nuova (nel nostro
     * caso non verrò mai chiamato)
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * Questa funzione è utilizzata per definire il comportamento in seguito ad uno swipe
     * Siccome nel nostro caso lo swipe serve solo per scoprire un menù aggiuntivo,
     * non è definito alcun comportamento per questa operazione
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    /**
     * Questa funzione definisce di quanto l'utente deve trascinare la card (in frazione) perché sia rilevato lo swipe
     * @param viewHolder il viewHolder che viene trascinato
     * @return la percentuale (in frazione) corrispondente a quanto deve essere trascinata la card per rilevare lo swipe
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return Integer.MAX_VALUE.toFloat()
    }

    /**
     * Questa funzione definisce la velocità minima con cui l'utente deve trascinare la
     * card perché sia rilevato lo swipe. La velocità è misurata in pixel al secondo
     * @param defaultValue valore di default della velocità usata dall'ItemTouchHelper
     * @return la velocità minima di swipe
     */
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return Integer.MAX_VALUE.toFloat()
    }

    /**
     * Questa funzione permette di definire il comportamento della View all'interazione dell'utente
     * con essa. Nel caso specifico, se lo swipe viene rilevato correttamente, compare a destra
     * della card swipata un menù in precedenza nascosto che permette di eliminarla
     * @param c canvas che permette di disegnare sui figli della recyclerView (non utilizzato)
     * @param recyclerView la recyclerView a cui è attaccata l'ItemTouchHelper
     * @param viewHolder il viewHolder con cui l'utente sta interagendo
     * @param dX indica lo spostamento orizzontale rispetto alla posizione originale causata dallo swipe
     * @param dX indica lo spostamento verticale rispetto alla posizione originale causata dallo swipe
     * @param actionState il tipo di interazione con la View. Nel nostro caso è solo ACTION_STATE_SWIPE
     * @param isCurrentlyActive indica se la View sta venendo controllata dall'utente
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if(dX == 0f) {
                currentScrollX = viewHolder.itemView.scrollX
                firstInactive = true
            }

            //controllo che l'utente stia facendo swipe
            if(isCurrentlyActive) {
                var scrollOffset = currentScrollX + (-dX).toInt()

                //calcolo di quanto è stato scrollato ed in che direzione,
                //per evitare comportamenti non previsti
                if(scrollOffset > limitScrollX)
                    scrollOffset = limitScrollX
                else
                    if(scrollOffset < 0)
                        scrollOffset = 0

                //si applica lo scroll alla view (in questo caso la card che l'utente ha trascinato)
                viewHolder.itemView.scrollTo(scrollOffset, 0)
            }
            else { //swipe fatto automaticamente senza l'intervento dell'utente
                if(firstInactive) {
                    firstInactive = false
                    currentScrollXWhenInactive = viewHolder.itemView.scrollX
                    initWhenActive = dX
                }

                //questo riporta la card alla posizione originaria se l'utente non ha trascinato la card
                //abbastanza perché fosse rilevato lo swipe
                if(viewHolder.itemView.scrollX < limitScrollX) {
                    viewHolder.itemView.scrollTo((currentScrollXWhenInactive * dX / initWhenActive).toInt(), 0)
                }
            }
        }
    }
}
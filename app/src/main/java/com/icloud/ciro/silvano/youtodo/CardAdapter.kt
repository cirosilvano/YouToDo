package com.icloud.ciro.silvano.youtodo

import android.animation.Animator
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.Card
import java.lang.Float.min
import java.time.LocalDateTime
import android.animation.AnimatorListenerAdapter
import android.widget.Button
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDateTime
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import java.time.LocalDate
import java.time.Period

/**
 * Questa classe ci permette di gestire le liste di Card ottenute tramite l'invocazione delle query select
 * sulla tabella card. Permette di creare oggetti ViewHolder e di settare i dati alle view
 */
class CardAdapter(val onItemSwipeListener: OnItemSwipeListener) :
    RecyclerView.Adapter<CardAdapter.MyViewHolder>() {

    private var cardList = emptyList<Card>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.itemName)
        val category: TextView = itemView.findViewById(R.id.itemCategory)
        val deadline: TextView = itemView.findViewById(R.id.itemDeadline)
        val cardLayout: MaterialCardView =itemView.findViewById(R.id.cardItem)

        /**
         * Funzione che permette di aggiornare gli elementi della UI relativa ad una card con i valori
         * associati all'elemento specifico della lista gestita dall'adapter
         * @param name_tx nome della card
         * @param category_tx categoria a cui appartiene la card
         * @param deadline_tx data di scadenza della card
         * @param checked stato della card (checked significa che l'evento della card è stato completato)
         */
        fun bind(name_tx: String, category_tx: String, deadline_tx: String, checked: Boolean) {
            name.text = name_tx
            category.text = category_tx
            cardLayout.isChecked=checked
            deadline.text = DateTimeFormatHelper.generateDeadlineString(deadline_tx, itemView.context)
        }
    }

    /**
     * Funzione che restituisce la dimensione della lista tenuta nell'adapter
     * @return numero di elementi contenuti nella lista tenuta dall'adapter
     * @see getItemCount nella classe Adapter
     */
    override fun getItemCount(): Int {
        return cardList.size
    }

    /**
     * Funzione che viene chiamata dalla recyclerView che necessita di creare un oggetto ViewHolder per rappresentare gli elementi dell'adapter
     * @param parent l'oggetto ViewGroup in cui sarà aggiunta la nuova View, una volta che sarà stata fissata ad una determinata posizione nell'adapter
     * @param viewType il tipo di vista della nuova View
     * @return un nuovo oggetto ViewHolder per contenere la nuova View
     * @see onCreateViewHolder nella classe Adapter
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        )
    }

    /**
     * Funzione che viene chiamata dalla recyclerView per mostrare i dati in una specifica posizione
     * @param holder il ViewHolder che viene aggiornato per rappresentare il contenuto della card alla posizione specificata nella lista dell'adapter
     * @param position posizione della card all'interno della lista dell'adapter
     * @see onBindViewHolder nella classe Adapter
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = cardList[position]

        holder.bind(
            currentItem.name,
            currentItem.category,
            currentItem.deadline,
            currentItem.isDone
        )

        // gestione del caso in cui si tenga premuto su una card
        holder.cardLayout.setOnLongClickListener {
            /* Invocazione della funzione dell'interfaccia implementata dal fragment in cui verra creata la
             * recyclerView che farà uso di questo adapter. Nel caso specifico, questo fragment è
             * MainFragment e la funzione permette di aggiornare lo stato della card (da unchecked a checked e viceversa)
             */
            onItemSwipeListener.onCheckCardClick(currentItem)

            /* setOnLongClickListener richiede come valore di ritorno un Boolean che specifica se
             * la callback ha consumato l'evento. Nel caso specifico, questo avverrà sempre con successo
             */
            true
        }

        // gestione del caso in cui si tenga premuto sul testo della card
        holder.name.setOnLongClickListener {
            /* Invocazione della funzione dell'interfaccia implementata dal fragment in cui verra creata la
             * recyclerView che farà uso di questo adapter. Nel caso specifico, questo fragment è
             * MainFragment e la funzione permette di aggiornare lo stato della card (da unchecked a checked e viceversa)
             */
            onItemSwipeListener.onCheckCardClick(currentItem)

            /* setOnLongClickListener richiede come valore di ritorno un Boolean che specifica se
             * la callback ha consumato l'evento. Nel caso specifico, questo avverrà sempre con successo
             */
            true
        }

        /* gestione del caso in cui si clicchi su una card. Nel caso specifico, il click permette
         * di passare al fragment in cui è possibile editare la specifica card
         */
        holder.cardLayout.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToEditFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }


        /* gestione del caso in cui si clicchi sul testo della card. Nel caso specifico, il click permette
         * di passare al fragment in cui è possibile editare la specifica card
         */
        holder.name.setOnClickListener{
            val action = MainFragmentDirections.actionMainFragmentToEditFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        /*
        holder.cardLayout.setOnTouchListener(View.OnTouchListener { view, event ->
            // variables to store current configuration of quote card.
            val displayMetrics = holder.cardLayout.context.resources.displayMetrics
            val cardWidth = holder.cardLayout.width
            val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val newX: Float = event.rawX
                    if (newX - cardWidth < cardStart) { // or newX < cardStart + cardWidth
                        holder.cardLayout.animate().x(
                            min(cardStart, newX - (cardWidth / 2))
                        )
                            .setDuration(0)
                            .start()
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    var currentX = holder.cardLayout.x
                    holder.cardLayout.animate()
                        .x(cardStart)
                        .setDuration(150)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                // check if the swipe distance was more than
                                // minimum swipe required to load a new quote
                                if (currentX < -80) {
                                    // Add logic to load a new quote if swiped adequately
                                    onItemSwipeListener.onCardSwipe(currentItem)
                                    currentX = 0f
                                }

                            }
                        })
                    true
                }
            }
            true

        })*/
    }

    /**
     * Funzione che permette di assegnare la lista di oggetti card che sarà gestita dall'adapter
     * @param card lista di oggetti Card che verrà gestita dall'adapter
     */
    fun setData(card: List<Card>) {
        this.cardList = card
        notifyDataSetChanged()
    }
}



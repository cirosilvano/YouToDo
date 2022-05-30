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

class CardAdapter(val onItemSwipeListener: OnItemSwipeListener) :
    RecyclerView.Adapter<CardAdapter.MyViewHolder>() {

    private var cardList = emptyList<Card>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.itemName)
        val category: TextView = itemView.findViewById(R.id.itemCategory)
        val deadline: TextView = itemView.findViewById(R.id.itemDeadline)
        val cardLayout: MaterialCardView =itemView.findViewById(R.id.cardItem)

        fun bind(name_tx: String, category_tx: String, deadline_tx: String, checked: Boolean) {
            name.text = name_tx
            category.text = category_tx
            cardLayout.isChecked=checked
            deadline.text = DateTimeFormatHelper.generateDeadlineString(deadline_tx, itemView.context)

        }
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = cardList[position]

        holder.bind(
            currentItem.name,
            currentItem.category,
            currentItem.deadline,
            currentItem.isDone
        )

        holder.cardLayout.setOnLongClickListener {
            onItemSwipeListener.onCheckCardClick(currentItem)
            true
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

        holder.cardLayout.setOnClickListener {
            val action = MainFragmentDirections.actionMainFragmentToEditFragment(currentItem)
            holder.itemView.findNavController().navigate(action)

        }
    }

    fun setData(card: List<Card>) {
        this.cardList = card
        notifyDataSetChanged()
    }
}



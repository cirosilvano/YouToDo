package com.icloud.ciro.silvano.youtodo

import android.animation.Animator
import android.database.DataSetObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.Item
import java.lang.Float.min
import java.time.LocalDateTime
import java.util.*
import android.animation.AnimatorListenerAdapter
import android.widget.ImageButton
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDate
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateDateTime
import com.icloud.ciro.silvano.youtodo.DateTimeFormatHelper.Companion.generateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.Period

class ItemAdapter(val onItemSwipeListener: OnItemSwipeListener) : RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    private var itemList= emptyList<Item>()

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.itemName)
        val category: TextView = itemView.findViewById(R.id.itemCategory)
        val deadline: TextView = itemView.findViewById(R.id.itemDeadline)
        val cardLayout : ConstraintLayout = itemView.findViewById(R.id.itemConstraintLayout)
        val done : CheckBox = itemView.findViewById(R.id.cbDone)
        val editCard : ImageButton = itemView.findViewById(R.id.editCard)

        fun bind(name_tx : String, category_tx : String, deadline_tx : String, checked : Boolean) {
            name.text = name_tx
            category.text = category_tx
            // deadline.text = deadline_tx
            done.isChecked = checked

            /* deadline styling */
            var deadline_gen = ""
            Log.d("", "deadline_tx for ${name_tx}: ${deadline_tx.length} -> $deadline_tx")
            if(deadline_tx.length == 19) {
                // date-time formats are 19 digits long
                val ldt = LocalDateTime.parse(deadline_tx)
                val ld = ldt.toLocalDate()
                val ldToday = LocalDate.now()
                val period = Period.between(ldToday, ld)

                if(period.years == 0 && period.months == 0) {
                    val res = itemView.context.resources
                    when (period.days) {
                        0 -> deadline_gen = "${res.getString(R.string.today)}, ${generateTime(ldt.hour, ldt.minute)}"
                        1 -> deadline_gen = "${res.getString(R.string.tomorrow)}, ${generateTime(ldt.hour, ldt.minute)}"
                        else -> {
                            if(period.days < 7) {
                                deadline_gen = "${DateTimeFormatHelper.weekDays(ld.dayOfWeek, res)}, ${generateTime(ldt.hour, ldt.minute)}"
                            }
                        }
                    }
                } else {
                    deadline_gen = generateDateTime(ldt.year, ldt.monthValue, ldt.dayOfMonth, ldt.hour, ldt.minute)
                }
            }
            deadline.text = deadline_gen
        }

        /*Swipe management*/
        /* val swipeGesture=object: SwipeGesture(){
             override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                 when(direction) {
                     ItemTouchHelper.LEFT -> {
                         val itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
                         itemViewModel.deleteItem(this.ite)
                     }
                     ItemTouchHelper.RIGHT->{

                     }
                 }
                 super.onSwiped(viewHolder, direction)
             }
         }*/
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = itemList[position]

        holder.bind(currentItem.name.toString(), currentItem.category.toString(), currentItem.deadline.toString(), currentItem.isDone)


        holder.editCard.setOnClickListener {
            //MainFragmentDirections is automatically generated (build in case it isn't found)
            val action = MainFragmentDirections.actionMainFragmentToEditFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        /*holder.cardLayout.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE ->{
                    onItemSwipeListener.onItemSwipe(currentItem)
                    true
                }
                //MotionEvent.ACTION-UP ->{
                view.performClick() //e questo fa andare il setOnClickListener
                }

            }
            return@OnTouchListener true
        })*/

        holder.cardLayout.setOnTouchListener(View.OnTouchListener { view, event ->
                // variables to store current configuration of quote card.
                val displayMetrics = holder.cardLayout.context.resources.displayMetrics
                val cardWidth = holder.cardLayout.width
                val cardStart = (displayMetrics.widthPixels.toFloat() / 2) - (cardWidth / 2)

                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        val newX : Float = event.rawX
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
                                    if (currentX < -250) {
                                        // Add logic to load a new quote if swiped adequately
                                        onItemSwipeListener.onItemSwipe(currentItem)
                                        currentX = 0f
                                    }

                                }
                            })
                        true
                    }
                }
                true

            }
        )

        holder.done.setOnClickListener {
            onItemSwipeListener.onItemTouchCheck(currentItem)
        }
    }

    fun setData(item: List<Item>) {
        this.itemList = item
        notifyDataSetChanged()
    }
}



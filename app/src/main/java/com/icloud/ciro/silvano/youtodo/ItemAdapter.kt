package com.icloud.ciro.silvano.youtodo

import android.database.DataSetObserver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.Item
import java.time.LocalDateTime
import java.util.*

class ItemAdapter(val onItemSwipeListener: OnItemSwipeListener) : RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    private var itemList= emptyList<Item>()

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.itemName)
        val category: TextView = itemView.findViewById(R.id.itemCategory)
        val deadline: TextView = itemView.findViewById(R.id.itemDeadline)
        val cardLayout : ConstraintLayout = itemView.findViewById(R.id.itemConstraintLayout)
        val done : CheckBox = itemView.findViewById(R.id.cbDone)

        fun bind(name_tx : String, category_tx : String, deadline_tx : String, checked : Boolean) {
            name.text = name_tx
            category.text = category_tx
            // deadline.text = deadline_tx
            done.isChecked = checked

            /* deadline styling */
            var deadline_gen = ""
            Log.d("", "deadline_tx for ${name_tx}: ${deadline_tx.length}")
            if(deadline_tx.length == 19) {
                // date-time formats are 19 digits long
                val ldt = LocalDateTime.parse(deadline_tx)
                val ldtToday = LocalDateTime.now()
                Log.d("","CompareTo: ${ldt.compareTo(ldtToday)}")
                var minuteString = ldt.minute.toString()
                if(minuteString.length == 1) minuteString = "0${minuteString}"
                when(ldt.compareTo(ldtToday)) {
                    -1 -> deadline_gen = "Today, ${ldt.hour}:${ldt.minute}"
                    1 -> deadline_gen = "Tomorrow, ${ldt.hour}:${ldt.minute}"
                    else -> deadline_gen = "${ldt.dayOfMonth}/${ldt.monthValue+1}/${ldt.year}, ${ldt.hour}:${minuteString}"
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

        holder.cardLayout.setOnClickListener {
            //MainFragmentDirections is automatically generated (build in case it isn't found)
            val action = MainFragmentDirections.actionMainFragmentToEditFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

        holder.done.setOnClickListener {
            onItemSwipeListener.onItemSwipe(currentItem)
        }
    }

    fun setData(item: List<Item>) {
        this.itemList = item
        notifyDataSetChanged()
    }
}



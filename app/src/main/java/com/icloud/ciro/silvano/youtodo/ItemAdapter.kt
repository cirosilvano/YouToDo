package com.example.youtodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.youtodo.databaseUtilities.Item
import com.icloud.ciro.silvano.youtodo.MainFragmentDirections
import com.icloud.ciro.silvano.youtodo.R

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    private var itemList = emptyList<Item>()

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.itemName)
        val category: TextView = itemView.findViewById(R.id.itemCategory)
        val deadline: TextView = itemView.findViewById(R.id.itemDeadline)
        val cardLayout : ConstraintLayout = itemView.findViewById(R.id.itemConstraintLayout)

        fun bind(name_tx : String, category_tx : String, deadline_tx : String) {
            name.text = name_tx
            category.text = category_tx
            deadline.text = deadline_tx
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem.name.toString(), currentItem.category.toString(), currentItem.deadline.toString())

        holder.cardLayout.setOnClickListener {
            //MainFragmentDirections is automatically generated (build in case it isn't found)
            val action = MainFragmentDirections.actionMainFragmentToEditFragment(currentItem)
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(item: List<Item>) {
        this.itemList = item
        notifyDataSetChanged()
    }

}
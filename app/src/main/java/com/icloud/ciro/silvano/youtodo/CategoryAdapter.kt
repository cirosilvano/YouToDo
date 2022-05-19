package com.icloud.ciro.silvano.youtodo

import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SpinnerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.Category
import com.icloud.ciro.silvano.youtodo.database.Item

class CategoryAdapter(private val onItemClicked:(Category)->Unit): RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    var listCat=emptyList<Category>()


    class MyViewHolder(categoryView : View) : RecyclerView.ViewHolder(categoryView){
        var btnCat=categoryView.findViewById<Button>(R.id.btnCat)

        /*Inizializzazione nome del bottone*/
        fun bind (name:String){
            btnCat.text=name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.MyViewHolder {
        return CategoryAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.button_category, parent, false)
        )

    }

    override fun onBindViewHolder(holder: CategoryAdapter.MyViewHolder, position: Int) {
        val currentCat=listCat[position]
        holder.bind(currentCat.name)
        holder.btnCat.setOnClickListener { onItemClicked(currentCat) }

    }

    override fun getItemCount(): Int {
        return listCat.size
    }

    fun setDataCat(cat: List<Category>) {
        this.listCat = cat
        notifyDataSetChanged()
    }



}


package com.icloud.ciro.silvano.youtodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.Category

class CategoryAdapter(val catListener: CategoryListener): RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    var listCat=emptyList<Category>()


    class MyViewHolder(categoryView : View) : RecyclerView.ViewHolder(categoryView){
        var btnModifyCat : ImageButton =categoryView.findViewById(R.id.btnModifyCat)
        var txtCatFrag : TextView =categoryView.findViewById(R.id.txtCatFrag)
        val btnDeleteCat : ImageButton = categoryView.findViewById(R.id.btnDeleteCat)

        /*Inizializzazione nome del bottone*/
        fun bind (name:String){
            //btnCat.text=name
            txtCatFrag.text=name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.MyViewHolder {
        return CategoryAdapter.MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.categorylist_item, parent, false)
        )

    }

    override fun onBindViewHolder(holder: CategoryAdapter.MyViewHolder, position: Int) {
        val currentCat=listCat[position]
        holder.bind(currentCat.name)
        holder.btnModifyCat.setOnClickListener { catListener.categoryEdit(currentCat) }
        holder.btnDeleteCat.setOnClickListener { catListener.categoryDelete(currentCat) }

    }

    override fun getItemCount(): Int {
        return listCat.size
    }

    fun setDataCat(cat: List<Category>) {
        this.listCat = cat
        notifyDataSetChanged()
    }



}


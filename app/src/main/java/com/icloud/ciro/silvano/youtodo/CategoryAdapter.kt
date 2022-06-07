package com.icloud.ciro.silvano.youtodo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.icloud.ciro.silvano.youtodo.database.Category

/**
 * Questa classe ci permette di gestire le liste di Card ottenute tramite l'invocazione delle query select
 * sulla tabella card. Permette di creare oggetti ViewHolder e di settare i dati alle view
 */
class CategoryAdapter(private val catListener: CategoryListener): RecyclerView.Adapter<CategoryAdapter.MyViewHolder>() {

    private var listCat = emptyList<Category>()

    class MyViewHolder(categoryView : View) : RecyclerView.ViewHolder(categoryView){
        private var txtCatFrag : TextView = categoryView.findViewById(R.id.txtCatFrag)

        var btnModifyCat : ImageButton = categoryView.findViewById(R.id.btnModifyCat)
        val btnDeleteCat : ImageButton = categoryView.findViewById(R.id.btnDeleteCat)

        /**
         * Funzione che permette di aggiornare gli elementi della UI relativa ad una card con i valori
         * associati all'elemento specifico della lista gestita dall'adapter
         * @param name nome della categoria
         */
        fun bind (name:String) {
            txtCatFrag.text=name
        }
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
            LayoutInflater.from(parent.context).inflate(R.layout.categorylist_item, parent, false)
        )
    }

    /**
     * Funzione che viene chiamata dalla recyclerView per mostrare i dati in una specifica posizione
     * @param holder il ViewHolder che viene aggiornato per rappresentare il contenuto della card alla posizione specificata nella lista dell'adapter
     * @param position posizione della card all'interno della lista dell'adapter
     * @see onBindViewHolder nella classe Adapter
     */
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentCat=listCat[position]

        holder.bind(currentCat.name)

        // gestione del caso in cui si prema sul bottone con l'icona della matita
        holder.btnModifyCat.setOnClickListener {
            /* Invocazione della funzione dell'interfaccia implementata dal fragment in cui verra creata la
             * recyclerView che farà uso di questo adapter. Nel caso specifico, questo fragment è
             * CategoryFragment e la funzione permette di aggiornare il nome della categoria
             */
            catListener.categoryEdit(currentCat)
        }

        // gestione del caso in cui si prema sul bottone con l'icona del bidone
        holder.btnDeleteCat.setOnClickListener {
            /* Invocazione della funzione dell'interfaccia implementata dal fragment in cui verra creata la
             * recyclerView che farà uso di questo adapter. Nel caso specifico, questo fragment è
             * CategoryFragment e la funzione permette di eliminare la categoria specifica
             */
            catListener.categoryDelete(currentCat)
        }
    }

    /**
     * Funzione che restituisce la dimensione della lista tenuta nell'adapter
     * @return numero di elementi contenuti nella lista tenuta dall'adapter
     * @see getItemCount nella classe Adapter
     */
    override fun getItemCount(): Int {
        return listCat.size
    }

    /**
     * Funzione che permette di assegnare la lista delle categorie che sarà gestita dall'adapter
     * @param cat lista di oggetti Category che verrà gestita dall'adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setDataCat(cat: List<Category>) {
        this.listCat = cat
        notifyDataSetChanged()
    }
}


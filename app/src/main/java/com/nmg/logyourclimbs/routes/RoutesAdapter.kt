package com.nmg.logyourclimbs.routes

import android.app.Activity

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log


import android.view.LayoutInflater
import android.view.ViewGroup

import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView
import com.nmg.logyourclimbs.R
import com.nmg.logyourclimbs.database.SqliteDatabase

internal class RoutesAdapter(private val context: Context, listRoutes: ArrayList<Routes>) : RecyclerView.Adapter<RoutesViewHolder>(), Filterable {
    var listRoutes: ArrayList<Routes>
    private val mArrayList: ArrayList<Routes>
    private val mDatabase: SqliteDatabase

    // Inicializo el adaptador con la lista de rutas y la base de datos
    init {
        this.listRoutes = listRoutes
        this.mArrayList = ArrayList(listRoutes)
        mDatabase = SqliteDatabase(context)
    }

    // Creo la vista para cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.route_list_layout, parent, false)
        return RoutesViewHolder(view)
    }

    // Asigno los valores a cada elemento de la lista
    override fun onBindViewHolder(holder: RoutesViewHolder, position: Int) {
        val routes = listRoutes[position]
        holder.tvRouteName.text = routes.routeName
        holder.tvGrade.text = routes.grade
        holder.tvRouteDescription.text = routes.description
        holder.tvAscentDate.text = routes.date
        // Determino el color basado en el valor de howWas
        val colorResId = when (routes.howWas) {
            "0" -> R.color.Onsight
            "1" -> R.color.Flash
            "2" -> R.color.Redpoint
            else -> android.R.color.black
        }
        val color = ContextCompat.getColor(holder.itemView.context, colorResId)
        holder.ivHowWas.setColorFilter(color, PorterDuff.Mode.SRC_IN)

        // Configuro el botón de eliminar ruta
        holder.ivDeleteRoute.setOnClickListener {
            val deleteDialog = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete_confirm))
                .setMessage(context.getString(R.string.cant_rollback))
                .setIcon(R.drawable.delete)
                .setPositiveButton(context.getString(R.string.accept)) { _, _ ->
                    Log.d("Noe", "El id de la ruta a eliminar es: ${routes.idRoute}")
                    mDatabase.deleteRoutes(routes.idRoute)
                    removeRouteAt(position)
                }
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ -> }
            deleteDialog.show()
        }
    }

    // Método para eliminar una ruta de la lista
    private fun removeRouteAt(position: Int) {
        listRoutes.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listRoutes.size)
    }

    // Implemento el filtro para buscar rutas
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                listRoutes = if (charString.isEmpty()) {
                    mArrayList
                } else {
                    val filteredList = ArrayList<Routes>()
                    for (route in mArrayList) {
                        if (route.routeName.lowercase().contains(charString.lowercase())) {
                            filteredList.add(route)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = listRoutes
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listRoutes = if (results != null && results.values != null) {
                    results.values as ArrayList<Routes>
                } else {
                    mArrayList
                }
                notifyDataSetChanged()
            }
        }
    }

    // Devuelvo el tamaño de la lista de rutas
    override fun getItemCount(): Int = listRoutes.size
}

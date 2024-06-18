package com.nmg.logyourclimbs.routes

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nmg.logyourclimbs.R

class RoutesViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    var tvGrade: TextView = itemView.findViewById(R.id.tvGrade)
    var tvAscentDate: TextView = itemView.findViewById(R.id.tvAscentDate)
    var tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
    var tvRouteDescription: TextView = itemView.findViewById(R.id.tvRouteDescription)
    var ivHowWas: ImageView = itemView.findViewById(R.id.ivHowWas)
    var ivDeleteRoute: ImageView = itemView.findViewById(R.id.ivDeleteRoute)
}
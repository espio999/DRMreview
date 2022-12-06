package com.example.drmreview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val drmParameter = itemView.findViewById<TextView>(R.id.drmParameter)
    val returnValue = itemView.findViewById<TextView>(R.id.returnValue)
    val description = itemView.findViewById<TextView>(R.id.description)
}
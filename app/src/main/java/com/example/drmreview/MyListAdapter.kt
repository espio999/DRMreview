package com.example.drmreview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyListAdapter(private val data: List<ListItem>) : RecyclerView.Adapter<MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.drmParameter.text = data[position].drmParameter
        holder.returnValue.text = data[position].returnValue
        holder.description.text = data[position].description
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
package com.example.grocerylistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.R
import com.example.grocerylistapp.model.UserItem

class ReadOnlyUserItemAdapter(
    private val items: List<UserItem>
) : RecyclerView.Adapter<ReadOnlyUserItemAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.item_name)
        val quantityTextView: TextView = itemView.findViewById(R.id.item_quantity)
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_list_readonly, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.quantityTextView.text = item.quantity.toString()
        holder.imageView.setImageResource(item.imageRes)
    }
}

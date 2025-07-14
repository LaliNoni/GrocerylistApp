package com.example.grocerylistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.R
import com.example.grocerylistapp.model.GroceryListModel

class UsersViewGroceryListsAdapter(
    private val lists: List<GroceryListModel>,
    private val onItemClick: (GroceryListModel) -> Unit
) : RecyclerView.Adapter<UsersViewGroceryListsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.list_name)
        val dateTextView: TextView = itemView.findViewById(R.id.list_date)
        val imageView: ImageView = itemView.findViewById(R.id.list_image)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(lists[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_user_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val list = lists[position]
        holder.nameTextView.text = list.name
        holder.dateTextView.text = list.date
        holder.imageView.setImageResource(R.drawable.list_placeholder)
    }
}

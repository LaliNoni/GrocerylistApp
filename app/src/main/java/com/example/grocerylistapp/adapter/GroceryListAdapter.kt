package com.example.grocerylistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.R
import com.example.grocerylistapp.database.ShoppingListRoom
import com.example.grocerylistapp.model.GroceryListModel

class GroceryListAdapter(
    private val lists: MutableList<ShoppingListRoom>,
    private val onCheckedChange: (Int, Boolean) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onListClick: (ShoppingListRoom) -> Unit
) : RecyclerView.Adapter<GroceryListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listName: TextView = itemView.findViewById(R.id.list_name)
        val listDate: TextView = itemView.findViewById(R.id.list_date)
        val listImage: ImageView = itemView.findViewById(R.id.list_image)
        val listChecked: CheckBox = itemView.findViewById(R.id.list_checked)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lists_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lists.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = lists[position]

        holder.listName.text = listItem.name
        holder.listDate.text = listItem.date

        holder.listImage.setImageResource(R.drawable.list_placeholder)
        holder.listChecked.setOnCheckedChangeListener(null)
        holder.listChecked.isChecked = listItem.isDone
        holder.listChecked.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(position, isChecked)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position)
        }

        holder.itemView.setOnClickListener {
            onListClick(listItem)
        }
    }
}
package com.example.grocerylistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.R
import com.example.grocerylistapp.model.GroceryItem


class ImagePickerAdapter(
    private val items: List<GroceryItem>,
    private val onItemClick: (GroceryItem, isSelected: Boolean) -> Unit
) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

    private val selectedPositions = mutableSetOf<Int>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.item_image_view)

        fun bind(item: GroceryItem, position: Int) {
            image.setImageResource(item.imageResId ?: R.drawable.chef)
            image.contentDescription = item.name

            // Show selection state (e.g., change alpha or border)
            image.alpha = if (selectedPositions.contains(position)) 0.5f else 1.0f

            image.setOnClickListener {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position)
                    image.alpha = 1.0f
                    onItemClick(item, false) // deselected
                } else {
                    selectedPositions.add(position)
                    image.alpha = 0.5f
                    onItemClick(item, true) // selected
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}


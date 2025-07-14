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

    private var selectedPosition: Int? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.item_image_view)

        fun bind(item: GroceryItem, position: Int) {
            image.setImageResource(item.imageRes ?: R.drawable.bake)
            image.contentDescription = item.name

            image.alpha = if (selectedPosition == position) 0.5f else 1.0f

            image.setOnClickListener {
                val previouslySelected = selectedPosition

                if (position == selectedPosition) {
                    selectedPosition = null
                    notifyItemChanged(position)
                    onItemClick(item, false)
                } else {

                    selectedPosition = position
                    notifyItemChanged(previouslySelected ?: -1)
                    notifyItemChanged(position)
                    onItemClick(item, true)
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


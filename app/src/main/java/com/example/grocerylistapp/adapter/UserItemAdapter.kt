package com.example.grocerylistapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.R
import com.example.grocerylistapp.model.UserItem

class UserItemAdapter(
    private val items: MutableList<UserItem>,
    private val onAddItemBelow: (position: Int) -> Unit
) : RecyclerView.Adapter<UserItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.item_image)
        private val name: EditText = view.findViewById(R.id.item_name)
        private val quantity: EditText = view.findViewById(R.id.item_quantity)
        private val checkBox: CheckBox = view.findViewById(R.id.item_checkbox)
        private val addButton: Button = view.findViewById(R.id.add_item_button)

        fun bind(item: UserItem, position: Int) {
            image.setImageResource(item.imageResId)
            name.setText(item.name)
            quantity.setText(item.quantity.toString())
            checkBox.isChecked = item.isChecked

            name.setOnFocusChangeListener { _, _ ->
                item.name = name.text.toString()
            }

            quantity.setOnFocusChangeListener { _, _ ->
                item.quantity = quantity.text.toString().toIntOrNull() ?: 0
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }

            addButton.setOnClickListener {
                onAddItemBelow(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_user_input, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}

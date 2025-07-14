package com.example.grocerylistapp.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerylistapp.R
import com.example.grocerylistapp.model.UserItem

class UserItemAdapter(
    private val items: MutableList<UserItem>,
    private val onAddItemBelow: (position: Int) -> Unit,
    private val onImageClick: (position: Int) -> Unit
) : RecyclerView.Adapter<UserItemAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.item_image)
        private val name: EditText = view.findViewById(R.id.item_name)
        private val quantity: EditText = view.findViewById(R.id.item_quantity)
        private val checkBox: CheckBox = view.findViewById(R.id.item_checkbox)
        private val addButton: Button = view.findViewById(R.id.add_item_button)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        private var nameWatcher: TextWatcher? = null
        private var quantityWatcher: TextWatcher? = null

        fun bind(item: UserItem, position: Int) {
            image.setImageResource(item.imageRes)
            checkBox.isChecked = item.isChecked

            nameWatcher?.let { name.removeTextChangedListener(it) }
            quantityWatcher?.let { quantity.removeTextChangedListener(it) }

            if (name.text.toString() != item.name) {
                name.setText(item.name)
            }
            val quantityStr = if (item.quantity > 0) item.quantity.toString() else ""
            if (quantity.text.toString() != quantityStr) {
                quantity.setText(quantityStr)
            }

            nameWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.name = s?.toString() ?: ""
                }
                override fun afterTextChanged(s: Editable?) {}
            }
            name.addTextChangedListener(nameWatcher)

            quantityWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    item.quantity = s?.toString()?.toIntOrNull() ?: 0
                }
                override fun afterTextChanged(s: Editable?) {}
            }
            quantity.addTextChangedListener(quantityWatcher)

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }

            addButton.setOnClickListener {
                onAddItemBelow(position)
            }

            image.setOnClickListener {
                if (item.imageRes != R.drawable.bake) {
                    item.imageRes = R.drawable.bake
                    notifyItemChanged(position)
                } else {
                    onImageClick(position)
                }
            }

            deleteButton.setOnClickListener {
                items.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, items.size)
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

package com.example.grocerylistapp.model
import com.example.grocerylistapp.R

data class UserItem(
    var name: String = "",
    var quantity: Int = 0,
    var imageRes: Int = R.drawable.bake,
    var isChecked: Boolean = false
)
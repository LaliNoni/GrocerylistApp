package com.example.grocerylistapp.model

import com.example.grocerylistapp.R

data class GroceryListModel(
    val name: String,
    val date: String,
    val imageResId: Int = R.drawable.list_placeholder,
    var isDone: Boolean = false
)
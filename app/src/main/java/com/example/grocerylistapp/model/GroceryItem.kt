package com.example.grocerylistapp.model

data class GroceryItem(
    val name: String,
    val quantity: String,
    val imageResId: Int? = null
)